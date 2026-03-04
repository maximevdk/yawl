package com.yawl.util;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class StringUtilsTest {

    private List<String> stringList;
    private Set<Integer> intSet;

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

    @Test
    void parse() throws NoSuchFieldException {
        var listType = getClass().getDeclaredField("stringList").getGenericType();
        var setType = getClass().getDeclaredField("intSet").getGenericType();

        assertThat((List<String>) StringUtils.parse(new String[]{"a,b", "c"}, listType))
                .containsExactly("a", "b", "c");
        assertThat((List<String>) StringUtils.parse(new String[]{}, listType)).isEmpty();
        assertThat((List<String>) StringUtils.parse(null, listType)).isEmpty();

        assertThat((Set<Integer>) StringUtils.parse(new String[]{"1,2", "3"}, setType))
                .containsExactlyInAnyOrder(1, 2, 3);

        assertThat((String) StringUtils.parse(new String[]{"hello"}, String.class)).isEqualTo("hello");
        assertThat((String) StringUtils.parse(null, String.class)).isNull();
    }

    @Test
    void objectToString() {
        assertThat(StringUtils.toString("1")).isEqualTo("1");
        assertThat(StringUtils.toString(1)).isEqualTo("1");
        assertThat(StringUtils.toString(1L)).isEqualTo("1");
        assertThat(StringUtils.toString(List.of(1, "2", 3))).isEqualTo("1,2,3");
        assertThat(StringUtils.toString(null)).isNull();
    }
}