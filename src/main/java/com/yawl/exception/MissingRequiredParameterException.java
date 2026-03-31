package com.yawl.exception;

/**
 * Thrown when a required query parameter is missing from the request.
 */
public class MissingRequiredParameterException extends ClientException {
    private MissingRequiredParameterException(String message) {
        super(message);
    }

    /**
     * Creates an exception for a missing required parameter.
     *
     * @param parameterName the missing parameter name
     * @return a new exception instance
     */
    public static MissingRequiredParameterException of(String parameterName) {
        return new MissingRequiredParameterException("Required parameter '%s' not found".formatted(parameterName));
    }
}
