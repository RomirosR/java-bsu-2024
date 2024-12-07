package by.bsu.dependency.context;

import by.bsu.dependency.example.*;
import by.bsu.dependency.exceptions.ApplicationContextNotStartedException;
import by.bsu.dependency.exceptions.NoSuchBeanDefinitionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SimpleApplicationContextTest {

    private ApplicationContext applicationContext;

    @BeforeEach
    void init() {
        applicationContext = new SimpleApplicationContext(FirstBean.class, OtherBean.class, SingletonWithPrototypesBean.class, PrototypeBean.class, OtherPrototypeBean.class);
    }

    @Test
    void testIsRunning() {
        assertThat(applicationContext.isRunning()).isFalse();
        applicationContext.start();
        assertThat(applicationContext.isRunning()).isTrue();
    }

    @Test
    void testContextContainsNotStarted() {
        assertThrows(
                ApplicationContextNotStartedException.class,
                () -> applicationContext.containsBean("firstBean")
        );
    }

    @Test
    void testContextContainsBeans() {
        applicationContext.start();

        assertThat(applicationContext.containsBean("firstBean")).isTrue();
        assertThat(applicationContext.containsBean("otherBean")).isTrue();
        assertThat(applicationContext.containsBean("singletonWithPrototypesBean")).isTrue();
        assertThat(applicationContext.containsBean("prototypeBean")).isTrue();
        assertThat(applicationContext.containsBean("otherPrototypeBean")).isTrue();
        assertThat(applicationContext.containsBean("randomName")).isFalse();
    }

    @Test
    void testContextGetBeanNotStarted() {
        assertThrows(
                ApplicationContextNotStartedException.class,
                () -> applicationContext.getBean("firstBean")
        );
    }

    @Test
    void testGetBeanReturns() {
        applicationContext.start();

        assertThat(applicationContext.getBean("firstBean")).isNotNull().isInstanceOf(FirstBean.class);
        assertThat(applicationContext.getBean("otherBean")).isNotNull().isInstanceOf(OtherBean.class);
        assertThat(applicationContext.getBean("singletonWithPrototypesBean")).isNotNull().isInstanceOf(SingletonWithPrototypesBean.class);
        assertThat(applicationContext.getBean("prototypeBean")).isNotNull().isInstanceOf(PrototypeBean.class);
        assertThat(applicationContext.getBean("otherPrototypeBean")).isNotNull().isInstanceOf(OtherPrototypeBean.class);
    }

    @Test
    void testGetBeanThrows() {
        applicationContext.start();

        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> applicationContext.getBean("randomName")
        );
    }

    @Test
    void testIsSingletonReturns() {
        assertThat(applicationContext.isSingleton("firstBean")).isTrue();
        assertThat(applicationContext.isSingleton("otherBean")).isTrue();
        assertThat(applicationContext.isSingleton("singletonWithPrototypesBean")).isTrue();
        assertThat(applicationContext.isSingleton("prototypeBean")).isFalse();
        assertThat(applicationContext.isSingleton("otherPrototypeBean")).isFalse();
    }

    @Test
    void testIsSingletonThrows() {
        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> applicationContext.isSingleton("randomName")
        );
    }

    @Test
    void testIsPrototypeReturns() {
        assertThat(applicationContext.isPrototype("firstBean")).isFalse();
        assertThat(applicationContext.isPrototype("otherBean")).isFalse();
        assertThat(applicationContext.isPrototype("singletonWithPrototypesBean")).isFalse();
        assertThat(applicationContext.isPrototype("prototypeBean")).isTrue();
        assertThat(applicationContext.isPrototype("otherPrototypeBean")).isTrue();
    }

    @Test
    void testIsPrototypeThrows() {
        assertThrows(
                NoSuchBeanDefinitionException.class,
                () -> applicationContext.isPrototype("randomName")
        );
    }

    @Test
    void testPrototypeCreatesNewInstance() {
        applicationContext.start();

        assertNotEquals(
                applicationContext.getBean("prototypeBean"),
                applicationContext.getBean("prototypeBean")
        );
    }

    @Test
    void testSingletonsInjectedCorrectly1() {
        applicationContext.start();

        var firstInstance = (PrototypeBean) applicationContext.getBean("prototypeBean");
        var secondInstance = (PrototypeBean) applicationContext.getBean("prototypeBean");

        assertEquals(firstInstance.firstBean, secondInstance.firstBean);
        assertEquals(firstInstance.otherBean, secondInstance.otherBean);
    }

    @Test
    void testSingletonsInjectedCorrectly2() {
        applicationContext.start();

        var firstInstance = (OtherPrototypeBean) applicationContext.getBean("otherPrototypeBean");
        var secondInstance = (OtherPrototypeBean) applicationContext.getBean("otherPrototypeBean");

        assertNotEquals(firstInstance, secondInstance);

        assertEquals(firstInstance.firstBean, secondInstance.firstBean);
        assertEquals(firstInstance.otherBean, secondInstance.otherBean);

        assertNotEquals(firstInstance.prototypeBean, secondInstance.prototypeBean);

        assertEquals(firstInstance.prototypeBean.firstBean, secondInstance.prototypeBean.firstBean);
        assertEquals(firstInstance.prototypeBean.otherBean, secondInstance.prototypeBean.otherBean);
    }

    @Test
    void testSingletonsInjectedCorrectly3() {
        applicationContext.start();

        var firstInstance = (SingletonWithPrototypesBean) applicationContext.getBean("singletonWithPrototypesBean");
        var secondInstance = (SingletonWithPrototypesBean) applicationContext.getBean("singletonWithPrototypesBean");

        assertEquals(firstInstance, secondInstance);
        assertEquals(firstInstance.prototypeBean, secondInstance.prototypeBean);
    }

    @Test
    void testPostConstruct() {
        applicationContext.start();

        assertThat(((FirstBean) applicationContext.getBean("firstBean")).isPostConstructed).isTrue();
        assertThat(((OtherBean) applicationContext.getBean("otherBean")).isPostConstructed).isTrue();
        var singletonWithPrototypesBean = (SingletonWithPrototypesBean) applicationContext.getBean("singletonWithPrototypesBean");
        assertFalse(singletonWithPrototypesBean.isPostConstructed);
        assertTrue(singletonWithPrototypesBean.prototypeBean.isPostConstructed);
    }
}