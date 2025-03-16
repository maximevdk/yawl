package com.yawl.exception;

public class RequiredRequestParameterMissingException extends RuntimeException {
    private static final String MESSAGE = "Required request parameter [%s] missing";

    public RequiredRequestParameterMissingException(String message) {
        super(message);
    }

    public static RequiredRequestParameterMissingException forParameter(String parameterName) {
        return new RequiredRequestParameterMissingException(MESSAGE.formatted(parameterName));
    }

}
