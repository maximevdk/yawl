package com.yawl.exception;

public class NoSuchMethodException extends RuntimeException {
    private static final String MESSAGE = "No such method with name %s on classType %s";

    private NoSuchMethodException(String message) {
        super(message);
    }

    public static NoSuchMethodException forMethodNameAndClass(String methodName, Class<?> clazz) {
        return new NoSuchMethodException(MESSAGE.formatted(methodName, clazz));
    }
}
