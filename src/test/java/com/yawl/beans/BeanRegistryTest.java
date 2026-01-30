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
        BeanRegistry.clear();
    }

    @Test
    void registerBeanAndGetBeanByName() {
        var testClass = new TestClass();
        BeanRegistry.registerBean("testClass", testClass);

        var result = BeanRegistry.getBeanByNameOrThrow("testClass");
        assertThat(result).isEqualTo(testClass);
    }

    @Test
    void registerBeanAndGetBeanByNameAndType() {
        var testClass = new TestClass();
        BeanRegistry.registerBean("testClass", testClass);

        var result = BeanRegistry.getBeanByNameOrThrow("testClass", TestClass.class);
        assertThat(result).isEqualTo(testClass);
    }

    @Test
    void registerBeanAndGetBeanByNameAndType_nonExistent_throws() {
        var testClass = new TestClass();
        BeanRegistry.registerBean("testClass", testClass);

        assertThatThrownBy(() -> BeanRegistry.getBeanByNameOrThrow("testClass", String.class))
                .isInstanceOf(NoSuchBeanException.class);
    }

    @Test
    void registerBeanAndGetBeanByName_nonExistent_throws() {
        var testClass = new TestClass();
        BeanRegistry.registerBean("testClass", testClass);

        assertThatThrownBy(() -> BeanRegistry.getBeanByNameOrThrow("testClazz"))
                .isInstanceOf(NoSuchBeanException.class);
    }

    @Test
    void registerBeanAndGetBeanByName_nonExistant_returnsEmpty() {
        var testClass = new TestClass();
        BeanRegistry.registerBean("testClass", testClass);

        assertThat(BeanRegistry.getBeanByName("testClazz")).isEmpty();
    }

    @Test
    void registerBeanAndFindBeanByType() {
        var testClass = new TestClass();
        BeanRegistry.registerBean("testClass", testClass);

        var result = BeanRegistry.findBeanByType(TestClass.class);
        assertThat(result).isPresent().contains(testClass);
    }

    @Test
    void containsBeanOfType() {
        var testClass = new TestClass();
        BeanRegistry.registerBean("testClass", testClass);

        assertThat(BeanRegistry.containsBeanOfType(TestClass.class)).isTrue();
        assertThat(BeanRegistry.containsBeanOfType(String.class)).isFalse();
    }
}