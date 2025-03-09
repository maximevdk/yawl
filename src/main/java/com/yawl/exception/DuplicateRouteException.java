package com.yawl.exception;

import com.yawl.model.Route;

public class DuplicateRouteException extends RuntimeException {
    private static final String MESSAGE = "More than one route defined [%s - %s]";

    private DuplicateRouteException(String message) {
        super(message);
    }

    public static DuplicateRouteException forRoute(Route route) {
        return new DuplicateRouteException(MESSAGE.formatted(route.method(), route.path()));
    }
}
