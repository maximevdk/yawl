package com.yawl.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StringUtilTest {

    @Test
    void test() {
        assertThat(StringUtil.decapitalize("Test")).isEqualTo("test");
        assertThat(StringUtil.decapitalize("test")).isEqualTo("test");
    }
}