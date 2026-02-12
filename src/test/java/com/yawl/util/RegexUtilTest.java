package com.yawl.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegexUtilTest {

    @Test
    void enabledManagementEndpointsAsStream() {
        assertEquals(List.of("a", "b"), RegexUtil.enabledManagementEndpointsAsStream("a, b").toList());
        assertEquals(List.of("a", "b"), RegexUtil.enabledManagementEndpointsAsStream("a,  b").toList());
        assertEquals(List.of("a", "b"), RegexUtil.enabledManagementEndpointsAsStream("a , b").toList());
        assertEquals(List.of("a"), RegexUtil.enabledManagementEndpointsAsStream("a").toList());
    }
}