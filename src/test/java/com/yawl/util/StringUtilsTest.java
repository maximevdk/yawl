package com.yawl.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StringUtilsTest {

    @Test
    void decapitalize() {
        assertThat(StringUtils.decapitalize("Test")).isEqualTo("test");
        assertThat(StringUtils.decapitalize("TEST")).isEqualTo("tEST");
        assertThat(StringUtils.decapitalize("test")).isEqualTo("test");
    }

    @Test
    void hasText() {
        assertThat(StringUtils.hasText(" a )")).isTrue();
        assertThat(StringUtils.hasText(") ")).isTrue();
        assertThat(StringUtils.hasText(" )")).isTrue();
        assertThat(StringUtils.hasText("")).isFalse();
        assertThat(StringUtils.hasText("        ")).isFalse();
    }
}