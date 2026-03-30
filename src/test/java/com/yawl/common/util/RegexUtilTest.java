package com.yawl.common.util;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RegexUtilTest {

    @Test
    void enabledManagementEndpointsAsStream() {
        assertEquals(List.of("a", "b"), RegexUtil.enabledManagementEndpointsAsStream("a, b").toList());
        assertEquals(List.of("a", "b"), RegexUtil.enabledManagementEndpointsAsStream("a,  b").toList());
        assertEquals(List.of("a", "b"), RegexUtil.enabledManagementEndpointsAsStream("a , b").toList());
        assertEquals(List.of("a"), RegexUtil.enabledManagementEndpointsAsStream("a").toList());
    }

    @Test
    void parseCommandLineArguments() {
        var arguments = RegexUtil.parseCommandLineArguments("--config.location=test.yml", "--yawl.profile.active=test", "--a=b", "-faulty=error");
        assertEquals(Map.of("config.location", "test.yml", "yawl.profile.active", "test", "a", "b"), arguments);
    }
}