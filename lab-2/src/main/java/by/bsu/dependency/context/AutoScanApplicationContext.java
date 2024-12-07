package by.bsu.dependency.context;

import by.bsu.dependency.annotation.Bean;
import org.reflections.Reflections;

import java.util.ArrayList;

public class AutoScanApplicationContext extends AbstractApplicationContext {

    /**
     * Создает контекст, содержащий классы из пакета {@code packageName}, помеченные аннотацией {@code @Bean}.
     * <br/>
     * Если имя бина в анноации не указано ({@code name} пустой), оно берется из названия класса.
     * <br/>
     * Подразумевается, что у всех классов, переданных в списке, есть конструктор без аргументов.
     *
     * @param packageName имя сканируемого пакета
     */
    public AutoScanApplicationContext(String packageName) {
        var reflections = new Reflections(packageName);
        var beanClasses = new ArrayList<>(reflections.getTypesAnnotatedWith(Bean.class));
        init(beanClasses);
    }
}
