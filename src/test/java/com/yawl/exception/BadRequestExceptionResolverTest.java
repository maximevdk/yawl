package com.yawl.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class BadRequestExceptionResolverTest {
    private final BadRequestExceptionResolver resolver = new BadRequestExceptionResolver();

    @Test
    void resolve_exceptionExtendsClientException_returns() {
        assertNotNull(resolver.resolve(MissingRequiredHeaderException.of("Bearer")));
        assertNotNull(resolver.resolve(MissingRequiredParameterException.of("id")));
        assertNotNull(resolver.resolve(MissingPathParameterException.of(null, "id")));
    }

    @Test
    void resolve_exceptionRuntimeException_returnsNull() {
        assertNull(resolver.resolve(new RuntimeException("null")));
    }
}