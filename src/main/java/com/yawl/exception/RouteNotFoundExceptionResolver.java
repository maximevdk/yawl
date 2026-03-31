package com.yawl.exception;

import com.yawl.http.model.HttpResponse;

/**
 * Resolves {@link RouteNotFoundException} instances into HTTP 404 Not Found responses.
 */
public class RouteNotFoundExceptionResolver implements ExceptionResolver {

    /** Creates a new instance. */
    public RouteNotFoundExceptionResolver() {}

    @Override
    public HttpResponse<?> resolve(Throwable exception) {
        if (exception instanceof RouteNotFoundException) {
            return HttpResponse.notFound(exception.getMessage());
        }

        return null;
    }
}
