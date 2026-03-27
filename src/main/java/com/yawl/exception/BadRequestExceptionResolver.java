package com.yawl.exception;

import com.yawl.http.model.HttpResponse;

public class BadRequestExceptionResolver implements ExceptionResolver {

    @Override
    public HttpResponse<?> resolve(Throwable exception) {
        if (exception instanceof ClientException) {
            return HttpResponse.badRequest(exception.getMessage());
        }

        return null;
    }
}
