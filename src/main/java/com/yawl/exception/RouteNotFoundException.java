package com.yawl.exception;

/**
 * Thrown when no route matches the incoming HTTP request.
 */
public class RouteNotFoundException extends RuntimeException {
    private RouteNotFoundException(String message) {
        super(message);
    }

    /**
     * Creates an exception for an unmatched route.
     *
     * @param httpMethod the HTTP method
     * @param path       the request path
     * @return a new exception instance
     */
    public static RouteNotFoundException notFound(String httpMethod, String path) {
        return new RouteNotFoundException("Route %s:%s not found".formatted(httpMethod, path));
    }
}
