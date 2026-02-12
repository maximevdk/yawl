package com.yawl.exception;

public class MissingRequiredParameterException extends RuntimeException {
    private MissingRequiredParameterException(String message) {
        super(message);
    }

    public static MissingRequiredParameterException of(String parameterName) {
        return new MissingRequiredParameterException("Required parameter '%s' not found".formatted(parameterName));
    }
}
