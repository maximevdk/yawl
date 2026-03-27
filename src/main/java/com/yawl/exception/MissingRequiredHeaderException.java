package com.yawl.exception;

public class MissingRequiredHeaderException extends ClientException {
    private MissingRequiredHeaderException(String message) {
        super(message);
    }

    public static MissingRequiredHeaderException of(String headerName) {
        return new MissingRequiredHeaderException("Required header '%s' is not present".formatted(headerName));
    }
}
