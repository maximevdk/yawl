package com.yawl.exception;

import com.yawl.http.model.Route;

/**
 * Thrown when a duplicate route definition is detected during route registration.
 */
public class DuplicateRouteException extends RuntimeException {
    private static final String MESSAGE = "More than one route defined [%s - %s]";

    private DuplicateRouteException(String message) {
        super(message);
    }

    /**
     * Creates an exception for a duplicate route.
     *
     * @param route the duplicate route
     * @return a new exception instance
     */
    public static DuplicateRouteException forRoute(Route route) {
        return new DuplicateRouteException(MESSAGE.formatted(route.method(), route.pattern()));
    }
}
