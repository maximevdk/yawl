package com.yawl.exception;

import com.yawl.http.model.HttpResponse;

import java.util.List;
import java.util.Objects;

/**
 * Handles exceptions thrown during request processing by delegating to registered {@link ExceptionResolver} instances.
 */
public class WebApplicationExceptionHandler {
    private static final String DEFAULT_ERROR_MESSAGE = "Something went wrong";
    private final List<ExceptionResolver> resolvers;

    /**
     * Creates a new handler with the given exception resolvers.
     *
     * @param resolvers the exception resolvers to try in order
     */
    public WebApplicationExceptionHandler(List<ExceptionResolver> resolvers) {
        this.resolvers = resolvers;
    }

    /**
     * Handles the given exception by finding the first resolver that produces a response.
     *
     * @param throwable the exception to handle
     * @return the resolved HTTP response, or a default 500 response if no resolver matches
     */
    public HttpResponse handle(Throwable throwable) {
        return resolvers.stream()
                .map(resolver -> resolver.resolve(throwable))
                .filter(Objects::nonNull)
                .findFirst()
                .orElse((HttpResponse) HttpResponse.internal(DEFAULT_ERROR_MESSAGE));
    }
}
