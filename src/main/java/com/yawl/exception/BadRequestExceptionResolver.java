package com.yawl.exception;

import com.yawl.http.model.HttpResponse;

/**
 * Resolves {@link ClientException} instances into HTTP 400 Bad Request responses.
 */
public class BadRequestExceptionResolver implements ExceptionResolver {

    @Override
    public HttpResponse<?> resolve(Throwable exception) {
        if (exception instanceof ClientException) {
            return HttpResponse.badRequest(exception.getMessage());
        }

        return null;
    }
}
