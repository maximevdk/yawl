package com.yawl.exception;

import com.yawl.http.model.HttpResponse;

public class RouteNotFoundExceptionResolver implements ExceptionResolver {

    @Override
    public HttpResponse<?> resolve(Throwable exception) {
        if (exception instanceof RouteNotFoundException) {
            return HttpResponse.notFound(exception.getMessage());
        }

        return null;
    }
}
