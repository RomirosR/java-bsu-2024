package by.bsu.dependency.example;

import by.bsu.dependency.annotation.Bean;
import by.bsu.dependency.annotation.Inject;

@Bean()
public class SingletonWithPrototypesBean {
    public boolean isPostConstructed = false;

    @Inject
    public PrototypeBean prototypeBean;
}
