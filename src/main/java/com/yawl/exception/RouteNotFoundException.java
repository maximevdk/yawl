package com.yawl.exception;

public class RouteNotFoundException extends RuntimeException {
    private RouteNotFoundException(String message) {
        super(message);
    }

    public static RouteNotFoundException notFound(String httpMethod, String path) {
        return new RouteNotFoundException("Route %s:%s not found".formatted(httpMethod, path));
    }
}
