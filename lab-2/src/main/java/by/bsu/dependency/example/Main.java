package by.bsu.dependency.example;

import by.bsu.dependency.context.ApplicationContext;
import by.bsu.dependency.context.AutoScanApplicationContext;

public class Main {

    public static void main(String[] args) {
        ApplicationContext applicationContext = new AutoScanApplicationContext(
                "by.bsu.dependency.example"
        );
        applicationContext.start();

        FirstBean firstBean = (FirstBean) applicationContext.getBean("firstBean");
        OtherBean otherBean = (OtherBean) applicationContext.getBean("otherBean");
        for (int i = 0; i < 5; i++) {
            System.out.printf("hashcode of new PrototypeBean: %d%n", applicationContext.getBean("prototypeBean").hashCode());
        }

        firstBean.doSomething();
        otherBean.doSomething();

        otherBean.doSomethingWithFirst();
    }
}
