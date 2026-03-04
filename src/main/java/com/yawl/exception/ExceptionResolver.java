package com.yawl.exception;

import com.yawl.http.model.HttpResponse;

public interface ExceptionResolver {
    /**
     * @return a HttpResponse if this resolver handles the exception,
     * or null if it does not apply.
     */
    HttpResponse<?> resolve(Throwable exception);
}
