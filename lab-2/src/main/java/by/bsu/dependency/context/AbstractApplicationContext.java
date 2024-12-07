package by.bsu.dependency.context;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractApplicationContext implements ApplicationContext {

    protected final Map<String, Object> beans = new HashMap<>();
    protected ContextStatus status = ContextStatus.NOT_STARTED;
    protected Map<String, BeanData> beanDefinitions = new HashMap<>();
    public AbstractApplicationContext(Class<?>... beanClasses) {
        init(new ArrayList<>(Arrays.asList(beanClasses)));
    }

    void init(ArrayList<Class<?>> beanClasses) {
        beanClasses.forEach(clazz -> beanDefinitions.put(BeanData.getName(clazz), new BeanData(clazz)));
    }

    /**
     * Помимо прочего, метод должен заниматься внедрением зависимостей в создаваемые объекты
     */
    @Override
    public void start() {
        status = ContextStatus.STARTED;

        beanDefinitions.forEach((beanName, beanData) -> {
            beans.put(beanName, instantiateBean(beanData.beanClass));
        });
        beans.forEach((beanName, bean) -> {
            if (BeanData.getScope(bean.getClass()) == BeanScope.SINGLETON) {
                recursiveInject(bean);
            }
        });
        beans.forEach((beanName, bean) -> {
            if (BeanData.getScope(bean.getClass()) == BeanScope.SINGLETON) {
                BeanData.executePostConstruct(bean);
            }
        });
    }

    void recursiveInject(Object bean) {
        for (var field : bean.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                try {
                    field.setAccessible(true);
                    field.set(bean, getBean(BeanData.getName(field.getType())));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private <T> T instantiateBean(Class<T> beanClass) {
        try {
            return beanClass.getConstructor().newInstance();
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 InstantiationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isRunning() {
        return status == ContextStatus.STARTED;
    }

    @Override
    public boolean containsBean(String name) {
        if (!isRunning()) {
            throw new ApplicationContextNotStartedException(name);
        }
        return beans.containsKey(name);
    }

    @Override
    public Object getBean(String name) {
        if (!isRunning()) {
            throw new ApplicationContextNotStartedException(name);
        }
        if (!containsBean(name)) {
            throw new NoSuchBeanDefinitionException(name);
        }
        if (beanDefinitions.get(name).scope == BeanScope.PROTOTYPE) {
            var created = instantiateBean(beanDefinitions.get(name).beanClass);
            recursiveInject(created);
            BeanData.executePostConstruct(created);
            return created;
        }
        return beans.get(name);
    }

    @Override
    public <T> T getBean(Class<T> clazz) {
        return clazz.cast(getBean(clazz.getAnnotation(Bean.class).name()));
    }

    @Override
    public boolean isPrototype(String name) {
        if (!beanDefinitions.containsKey(name)) {
            throw new NoSuchBeanDefinitionException(name);
        }
        return beanDefinitions.get(name).scope == BeanScope.PROTOTYPE;
    }

    @Override
    public boolean isSingleton(String name) {
        if (!beanDefinitions.containsKey(name)) {
            throw new NoSuchBeanDefinitionException(name);
        }
        return beanDefinitions.get(name).scope == BeanScope.SINGLETON;
    }

    protected enum ContextStatus {
        NOT_STARTED,
        STARTED
    }
}
