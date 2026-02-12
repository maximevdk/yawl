package com.yawl.model;

import com.yawl.http.model.PathPattern;
import com.yawl.http.model.PathSegment;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PathPatternTest {
    @Test
    void parse() {
        var actual = PathPattern.parse("/users/{id}/profile/{type}");
        var expected = new PathPattern(List.of(new PathSegment.Literal("users"),
                new PathSegment.Capture("id"),
                new PathSegment.Literal("profile"),
                new PathSegment.Capture("type")));
        assertEquals(expected, actual, "Expected PathPattern to match");
    }

    @Test
    void matches() {
        var pattern = PathPattern.parse("/users/{id}/profile/{type}");

        assertTrue(pattern.matches("/users/1/profile/public"), "Capture group can be anything");
        assertTrue(pattern.matches("/users/1/profile/private"), "Capture group can be anything");
        assertTrue(pattern.matches("users/2/profile/public"));
        assertTrue(pattern.matches("users/1/profile/public/"));
        assertFalse(pattern.matches("/users/1/profile/"));
        assertFalse(pattern.matches("users/1/profile/"));
        assertFalse(pattern.matches("/user/1/profile/public"));
    }

    @Test
    void extractPathVariables() {
        var pattern = PathPattern.parse("/users/{id}/profile/{type}");

        assertEquals(Map.of("id", "1", "type", "public"), pattern.extractPathVariables("/users/1/profile/public"));
        assertEquals(Map.of("id", "1", "type", "public"), pattern.extractPathVariables("users/1/profile/public"));
        assertEquals(Map.of("id", "1", "type", "public"), pattern.extractPathVariables("users/1/profile/public/"));
        assertEquals(Map.of(), pattern.extractPathVariables("/users/profile/public"));
    }

    @Test
    void captureNames() {
        var pattern = PathPattern.parse("/users/{id}/profile/{type}");

        assertEquals(List.of("id", "type"), pattern.captureNames());
    }
}