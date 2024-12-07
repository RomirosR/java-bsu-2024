package by.bsu.dependency.example;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.BeanScope;
import by.bsu.dependency.annotation.Inject;
import by.bsu.dependency.annotation.PostConstruct;

@Bean(name = "otherPrototypeBean", scope = BeanScope.PROTOTYPE)
public class OtherPrototypeBean {
    public boolean isPostConstructed = false;

    @Inject
    public FirstBean firstBean;

    @Inject
    public OtherBean otherBean;

    @Inject
    public PrototypeBean prototypeBean;

    @PostConstruct
    void postConstruct() {
        this.isPostConstructed = true;
    }
}