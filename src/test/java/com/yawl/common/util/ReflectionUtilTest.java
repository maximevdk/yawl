package com.yawl.common.util;

import com.yawl.TestClass;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReflectionUtilTest {
    @Test
    void invoke() throws Exception {
        var input = new TestClass("1", 1, true, 1.0);
        var toStringMethod = input.getClass().getMethod("toString");
        var result = ReflectionUtil.invoke(toStringMethod, input, List.of());

        assertThat(result)
                .isPresent()
                .contains("TestClass{var1='1', var2=1, var3=true, var4=1.0}");
    }

    @Test
    void invokeVoidMethod_returnsEmpty() throws Exception {
        var input = new TestClass("1", 1, true, 1.0);
        var doTheDoMethod = input.getClass().getMethod("doTheDo");
        assertThat(ReflectionUtil.invoke(doTheDoMethod, input, List.of())).isEmpty();
    }

    @Test
    void invokeNonExistingMethod_returnsEmpty() throws Exception {
        var input = new TestClass("1", 1, true, 1.0);
        var doTheDoMethod = getClass().getMethod("doTheDo");
        assertThat(ReflectionUtil.invoke(doTheDoMethod, input, List.of())).isEmpty();
    }

    @Test
    void newInstance() {
        var expected = new TestClass("1", 1, true, 1.0);

        assertThat(ReflectionUtil.newInstance(TestClass.class, "1", 1, true, 1.0))
                .isPresent()
                .get()
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void newInstance_correctArguments_differentOrder_throws() {
        assertThatThrownBy(() -> ReflectionUtil.newInstance(TestClass.class, "1", true, 1, 1.0))
                .isInstanceOf(java.lang.IllegalArgumentException.class)
                .hasMessage("argument type mismatch");
    }

    public void doTheDo() {
    }
}