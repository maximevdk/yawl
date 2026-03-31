package com.yawl.exception;

import com.yawl.http.model.Route;

/**
 * Thrown when a required path parameter is missing from the request URI.
 */
public class MissingPathParameterException extends ClientException {
    private static final String MESSAGE = "Missing @PathParam [%s] annotation for route [%s]";

    private MissingPathParameterException(String message) {
        super(message);
    }

    /**
     * Creates an exception for a missing path parameter.
     *
     * @param route the route definition
     * @param param the missing parameter name
     * @return a new exception instance
     */
    public static MissingPathParameterException of(Route route, String param) {
        return new MissingPathParameterException(MESSAGE.formatted(param, route));
    }
}
