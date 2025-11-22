package com.yawl.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RegexUtilTest {

    @Test
    void enabledManagementEndpointsAsStream() {
        assertEquals(List.of("a", "b"), RegexUtil.enabledManagementEndpointsAsStream("a, b").toList());
        assertEquals(List.of("a", "b"), RegexUtil.enabledManagementEndpointsAsStream("a,  b").toList());
        assertEquals(List.of("a", "b"), RegexUtil.enabledManagementEndpointsAsStream("a , b").toList());
        assertEquals(List.of("a"), RegexUtil.enabledManagementEndpointsAsStream("a").toList());
    }

    @Test
    void pathParam() {
        assertTrue(RegexUtil.pathParam("/route/{param}").find());
        assertFalse(RegexUtil.pathParam("/route/test").find());
        assertTrue(RegexUtil.pathParam("/route/test/{param}").find());
    }

    @Test
    void pathParamsToRegex() {
        assertTrue(RegexUtil.pathParamsToRegex("/route/{param}").matcher("/route/1").matches());
        assertTrue(RegexUtil.pathParamsToRegex("/route/{param}").matcher("/route/yes").matches());
        assertFalse(RegexUtil.pathParamsToRegex("/route/{param}").matcher("/route/yes/butno").matches());
    }
}