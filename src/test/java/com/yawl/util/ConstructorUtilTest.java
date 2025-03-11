package com.yawl.util;

import com.yawl.TestClass;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ConstructorUtilTest {
    @Test
    void test() {
        var result = ConstructorUtil.getRequiredConstructorParameters(TestClass.class);

        assertThat(result).containsExactly(String.class, int.class, boolean.class, Double.class);

        var instance = ConstructorUtil.newInstance(TestClass.class, "test", 1, true, 1.0);
        assertThat(instance).isNotNull();
    }
}