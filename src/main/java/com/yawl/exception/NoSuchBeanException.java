package com.yawl.exception;

public class NoSuchBeanException extends RuntimeException {
    private static final String MESSAGE = "No bean of type %s found";
    private static final String MESSAGE_BY_NAME = "No bean with name %s found";

    private NoSuchBeanException(String message) {
        super(message);
    }

    public static NoSuchBeanException forClass(Class<?> clazz) {
        return new NoSuchBeanException(MESSAGE.formatted(clazz));
    }

    public static NoSuchBeanException forName(String name) {
        return new NoSuchBeanException(MESSAGE_BY_NAME.formatted(name));
    }
}
