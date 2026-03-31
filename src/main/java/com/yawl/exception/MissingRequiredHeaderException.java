package com.yawl.exception;

/**
 * Thrown when a required HTTP request header is missing.
 */
public class MissingRequiredHeaderException extends ClientException {
    private MissingRequiredHeaderException(String message) {
        super(message);
    }

    /**
     * Creates an exception for a missing required header.
     *
     * @param headerName the missing header name
     * @return a new exception instance
     */
    public static MissingRequiredHeaderException of(String headerName) {
        return new MissingRequiredHeaderException("Required header '%s' is not present".formatted(headerName));
    }
}
