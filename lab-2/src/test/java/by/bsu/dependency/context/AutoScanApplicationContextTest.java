package by.bsu.dependency.context;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AutoScanApplicationContextTest {
    private ApplicationContext applicationContext;

    @BeforeEach
    void init() {
        applicationContext = new AutoScanApplicationContext("by.bsu.dependency.example");
    }

    @Test
    void testThereAreOnlyBeansScanned() {
        applicationContext.start();

        assertTrue(applicationContext.containsBean("firstBean"));
        assertTrue(applicationContext.containsBean("otherBean"));
        assertTrue(applicationContext.containsBean("prototypeBean"));
        assertTrue(applicationContext.containsBean("otherPrototypeBean"));
        assertTrue(applicationContext.containsBean("singletonWithPrototypesBean"));
        assertFalse(applicationContext.containsBean("notBean"));
        assertFalse(applicationContext.containsBean("main"));
    }
}
