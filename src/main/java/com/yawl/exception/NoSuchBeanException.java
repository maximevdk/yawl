package com.yawl.exception;

public class NoSuchBeanException extends RuntimeException {
    private static final String MESSAGE = "No bean of type %s found";

    private NoSuchBeanException(String message) {
        super(message);
    }

    public static NoSuchBeanException forClass(Class<?> clazz) {
        return new NoSuchBeanException(MESSAGE.formatted(clazz));
    }
}
