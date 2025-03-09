package com.yawl.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RouteTest {

    @Test
    void testToString() {
        assertEquals("GET:/test/2", new Route(HttpMethod.GET, "/test/2").toString());
    }

    @ParameterizedTest
    @MethodSource("provideStringsForRouteOf")
    void of(String[] input, String expectedResult) {
        var route = Route.of(HttpMethod.GET, input);
        assertEquals(expectedResult, route.path());
    }

    private static Stream<Arguments> provideStringsForRouteOf() {
        return Stream.of(
                Arguments.of(new String[]{"/test"}, "/test"),
                Arguments.of(new String[]{"test"}, "/test"),
                Arguments.of(new String[]{"/", "test1/{pathParam}"}, "/test1/{pathParam}"),
                Arguments.of(new String[]{"/", "/test1", "/test3", "{pathParam}"}, "/test1/test3/{pathParam}")
        );
    }
}