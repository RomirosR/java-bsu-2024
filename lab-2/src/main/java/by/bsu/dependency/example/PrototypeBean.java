package by.bsu.dependency.example;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;

@Bean(name = "prototypeBean", scope = BeanScope.PROTOTYPE)
public class PrototypeBean {
    public static int counter = 0;
    public boolean isPostConstructed = false;
    @Inject
    public FirstBean firstBean;
    @Inject
    public OtherBean otherBean;

    @PostConstruct
    void postConstruct() {
        this.isPostConstructed = true;
    }
}