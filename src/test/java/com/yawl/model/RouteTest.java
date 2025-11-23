package com.yawl.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class RouteTest {

    @Test
    void testToString() {
        assertEquals("GET:/test/2", new Route(HttpMethod.GET, "/test/2").toString());
    }

    @Test
    void hasPathParam() {
        assertFalse(new Route(HttpMethod.GET, "/test/2").hasPathParam());
        assertTrue(new Route(HttpMethod.GET, "/test/{id}").hasPathParam());
        assertTrue(new Route(HttpMethod.GET, "/test/{id}/{anotherParam}").hasPathParam());
        assertTrue(new Route(HttpMethod.GET, "/test/{id}/handle").hasPathParam());
    }

    @Test
    void equals() {
        assertEquals(new Route(HttpMethod.GET, "/test/{id}"), new Route(HttpMethod.GET, "/test/1"));
        assertEquals(new Route(HttpMethod.GET, "/test/{id}"), new Route(HttpMethod.GET, "/test/value"));
        assertNotEquals(new Route(HttpMethod.GET, "/test/{id}"), new Route(HttpMethod.GET, "/test/value/test2"));
        assertNotEquals(new Route(HttpMethod.GET, "/test/{id}"), new Route(HttpMethod.GET, "/tester/1"));
    }

    @Test
    void pathParams() {
        assertEquals(List.of(), new Route(HttpMethod.GET, "/test/2").pathParams());
        assertEquals(List.of("id"), new Route(HttpMethod.GET, "/test/{id}").pathParams());
        assertEquals(List.of("id", "anotherParam"), new Route(HttpMethod.GET, "/test/{id}/{anotherParam}").pathParams());
        assertEquals(List.of("id"), new Route(HttpMethod.GET, "/test/{id}/test2").pathParams());
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