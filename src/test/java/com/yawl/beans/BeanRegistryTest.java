package com.yawl.beans;

import com.yawl.TestClass;
import com.yawl.exception.NoSuchBeanException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BeanRegistryTest {

    private final BeanRegistry registry = new BeanRegistry();

    @AfterEach
    void tearDown() throws NoSuchFieldException, IllegalAccessException {
        registry.clear();
    }

    @Test
    void registerBeanAndGetBeanByName() {
        var testClass = new TestClass();
        registry.registerBean("testClass", testClass, testClass.getClass());

        var result = registry.getBeanByNameOrThrow("testClass");
        assertThat(result).isEqualTo(testClass);
    }

    @Test
    void registerBeanAndGetBeanByNameAndType() {
        var testClass = new TestClass();
        registry.registerBean("testClass", testClass, testClass.getClass());

        var result = registry.getBeanByNameOrThrow("testClass", TestClass.class);
        assertThat(result).isEqualTo(testClass);
    }

    @Test
    void registerBeanAndGetBeanByNameAndType_nonExistent_throws() {
        var testClass = new TestClass();
        registry.registerBean("testClass", testClass, testClass.getClass());

        assertThatThrownBy(() -> registry.getBeanByNameOrThrow("testClass", String.class))
                .isInstanceOf(NoSuchBeanException.class);
    }

    @Test
    void registerBeanAndGetBeanByName_nonExistent_throws() {
        var testClass = new TestClass();
        registry.registerBean("testClass", testClass, testClass.getClass());

        assertThatThrownBy(() -> registry.getBeanByNameOrThrow("testClazz"))
                .isInstanceOf(NoSuchBeanException.class);
    }

    @Test
    void registerBeanAndGetBeanByName_nonExistant_returnsEmpty() {
        var testClass = new TestClass();
        registry.registerBean("testClass", testClass, testClass.getClass());

        assertThat(registry.getBeanByName("testClazz")).isEmpty();
    }

    @Test
    void registerBeanAndFindBeanByType() {
        var testClass = new TestClass();
        registry.registerBean("testClass", testClass, testClass.getClass());

        var result = registry.findBeanByType(TestClass.class);
        assertThat(result).isPresent().contains(testClass);
    }

    @Test
    void containsBeanOfType() {
        var testClass = new TestClass();
        registry.registerBean("testClass", testClass, testClass.getClass());

        assertThat(registry.containsBeanOfType(TestClass.class)).isTrue();
        assertThat(registry.containsBeanOfType(String.class)).isFalse();
    }
}