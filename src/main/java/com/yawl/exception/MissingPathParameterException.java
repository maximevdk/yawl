package com.yawl.exception;

import com.yawl.model.Route;

public class MissingPathParameterException extends RuntimeException {
    private static final String MESSAGE = "Missing @PathParam [%s] annotation for route [%s]";

    private MissingPathParameterException(String message) {
        super(message);
    }

    public static MissingPathParameterException forPath(Route route, String param) {
        return new MissingPathParameterException(MESSAGE.formatted(param, route));
    }
}
