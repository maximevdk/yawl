package com.yawl.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RouteNotFoundExceptionResolverTest {
    private final RouteNotFoundExceptionResolver resolver = new RouteNotFoundExceptionResolver();

    @Test
    void resolve() {
        assertThat(resolver.resolve(RouteNotFoundException.notFound("GET", "/test"))).isNotNull();
        assertThat(resolver.resolve(new RuntimeException("test"))).isNull();
    }
}