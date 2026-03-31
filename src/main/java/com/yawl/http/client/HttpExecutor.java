package com.yawl.http.client;

import java.lang.reflect.Type;

/**
 * Abstraction for executing HTTP requests and deserializing responses.
 */
public interface HttpExecutor {
    /**
     * Executes the given HTTP request and deserializes the response into the specified type.
     *
     * @param request    the HTTP request to execute
     * @param returnType the expected return type of the response
     * @param <T>        the response type
     * @return the deserialized response
     */
    <T> T execute(HttpRequest request, Type returnType);
}
