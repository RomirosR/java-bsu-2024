package by.bsu.dependency.context;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.PostConstruct;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class BeanData {
    public Class<?> beanClass;
    public BeanScope scope;

    BeanData(Class<?> beanClass) {
        this.beanClass = beanClass;
        this.scope = getScope(beanClass);
    }

    public static String getName(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Bean.class) && !clazz.getAnnotation(Bean.class).name().isEmpty()) {
            return clazz.getAnnotation(Bean.class).name();
        }
        String name = clazz.getAnnotation(Bean.class).name();
        if (name.isEmpty()) {
            name = Arrays.asList(clazz.getName().split("\\.")).getLast();
        }
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }

    public static BeanScope getScope(Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Bean.class)) {
            return BeanScope.SINGLETON;
        }
        return clazz.getAnnotation(Bean.class).scope();
    }

    public static void executePostConstruct(Object bean) {
        var methods = Arrays.stream(bean.getClass().getDeclaredMethods()).filter(method -> method.isAnnotationPresent(PostConstruct.class)).toList();
        if (methods.size() > 1) {
            throw new RuntimeException("Too many PostConstruct methods");
        }
        if (!methods.isEmpty()) {
            try {
                methods.getFirst().setAccessible(true);
                methods.getFirst().invoke(bean);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
