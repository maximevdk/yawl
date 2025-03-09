package com.yawl.exception;

public class InvalidContextException extends RuntimeException {

    public InvalidContextException(String message, Throwable cause) {
        super(message, cause);
    }
}
