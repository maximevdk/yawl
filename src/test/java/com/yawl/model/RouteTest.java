package com.yawl.model;

import com.yawl.http.model.HttpMethod;
import com.yawl.http.model.Route;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RouteTest {

    @Test
    void matches() {
        assertTrue(Route.of(HttpMethod.GET, "/test/{id}").matches(HttpMethod.GET, "/test/1"));
        assertTrue(Route.of(HttpMethod.GET, "/test/{id}").matches(HttpMethod.GET, "/test/value"));
        assertFalse(Route.of(HttpMethod.POST, "/test/{id}").matches(HttpMethod.GET, "/test/value"));
        assertFalse(Route.of(HttpMethod.GET, "/test/{id}").matches(HttpMethod.GET, "/test/value/3"));
    }

    @Test
    void pathParams() {
        assertEquals(List.of(), Route.of(HttpMethod.GET, "/test/2").pathParamNames());
        assertEquals(List.of("id"), Route.of(HttpMethod.GET, "/test/{id}").pathParamNames());
        assertEquals(List.of("id", "anotherParam"), Route.of(HttpMethod.GET, "/test/{id}/{anotherParam}").pathParamNames());
        assertEquals(List.of("id"), Route.of(HttpMethod.GET, "/test/{id}/test2").pathParamNames());
    }
}