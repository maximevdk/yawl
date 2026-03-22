package com.yawl.exception;

import com.yawl.http.model.HttpResponse;

/**
 * ExceptionResolver interface to be implemented to transform an exception into an @{com.yawl.http.model.HttpResponse}
 */
public interface ExceptionResolver {
    /**
     * Resolve an exception and map it into a corresponding @{com.yawl.http.model.HttpResponse}
     * @return a HttpResponse if this resolver handles the exception,
     * or null if it does not apply.
     */
    HttpResponse<?> resolve(Throwable exception);
}
