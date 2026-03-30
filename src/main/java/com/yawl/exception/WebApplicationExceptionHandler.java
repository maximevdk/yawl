package com.yawl.exception;

import com.yawl.http.model.HttpResponse;

import java.util.List;
import java.util.Objects;

public class WebApplicationExceptionHandler {
    private static final String DEFAULT_ERROR_MESSAGE = "Something went wrong";
    private final List<ExceptionResolver> resolvers;

    public WebApplicationExceptionHandler(List<ExceptionResolver> resolvers) {
        this.resolvers = resolvers;
    }

    public HttpResponse handle(Throwable throwable) {
        return resolvers.stream()
                .map(resolver -> resolver.resolve(throwable))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse((HttpResponse) HttpResponse.internal(DEFAULT_ERROR_MESSAGE));
    }
}
