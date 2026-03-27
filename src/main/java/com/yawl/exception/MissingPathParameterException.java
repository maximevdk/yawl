package com.yawl.exception;

import com.yawl.http.model.Route;

public class MissingPathParameterException extends ClientException {
    private static final String MESSAGE = "Missing @PathParam [%s] annotation for route [%s]";

    private MissingPathParameterException(String message) {
        super(message);
    }

    public static MissingPathParameterException of(Route route, String param) {
        return new MissingPathParameterException(MESSAGE.formatted(param, route));
    }
}
