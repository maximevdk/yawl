package com.yawl.util;

import com.yawl.TestClass;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Parameter;

import static org.assertj.core.api.Assertions.assertThat;

class ConstructorUtilTest {
    @Test
    void test() {
        var result = ConstructorUtil.getRequiredConstructorParameters(TestClass.class);

        assertThat(result).hasSize(4);
        assertThat(result)
                .extracting(Parameter::getParameterizedType)
                .containsExactly(String.class, int.class, boolean.class, Double.class);
        assertThat(result)
                .extracting(Parameter::getName)
                .containsExactly("var1", "var2", "var3", "var4");

        var instance = ConstructorUtil.newInstance(TestClass.class, "test", 1, true, 1.0);
        assertThat(instance).isNotEmpty();
    }
}