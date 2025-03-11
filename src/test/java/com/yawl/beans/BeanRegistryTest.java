package com.yawl.beans;

import com.yawl.TestClass;
import com.yawl.exception.NoSuchBeanException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BeanRegistryTest {

    @AfterEach
    void tearDown() throws NoSuchFieldException, IllegalAccessException {
        BeanRegistry.registerBean("testClass", null);
    }

    @Test
    void registerBeanAndGetBeanByName() {
        var testClass = new TestClass();
        BeanRegistry.registerBean("testClass", testClass);

        var result = BeanRegistry.getBeanByName("testClass");
        assertThat(result).isEqualTo(testClass);
    }

    @Test
    void registerBeanAndGetBeanByNameAndType() {
        var testClass = new TestClass();
        BeanRegistry.registerBean("testClass", testClass);

        var result = BeanRegistry.getBeanByName("testClass", TestClass.class);
        assertThat(result).isEqualTo(testClass);
    }

    @Test
    void registerBeanAndGetBeanByNameAndType_nonExistant_throws() {
        var testClass = new TestClass();
        BeanRegistry.registerBean("testClass", testClass);

        assertThatThrownBy(() -> BeanRegistry.getBeanByName("testClass", String.class))
                .isInstanceOf(NoSuchBeanException.class);
    }

    @Test
    void registerBeanAndFindBeanByType() {
        var testClass = new TestClass();
        BeanRegistry.registerBean("testClass", testClass);

        var result = BeanRegistry.findBeanByType(TestClass.class);
        assertThat(result).isEqualTo(testClass);
    }
}