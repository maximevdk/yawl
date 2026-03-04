package com.yawl.exception;

import com.yawl.http.model.HttpResponse;

public class FallbackExceptionResolver implements ExceptionResolver {
    @Override
    public HttpResponse<?> resolve(Throwable exception) {
        return HttpResponse.internal(exception.getMessage());
    }
}
