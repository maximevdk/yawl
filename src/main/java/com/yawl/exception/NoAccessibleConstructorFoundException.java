package com.yawl.exception;

public class NoAccessibleConstructorFoundException extends RuntimeException {
    private static final String MESSAGE = "No accessible constructor found for class [%s]";

    private NoAccessibleConstructorFoundException(String message) {
        super(message);
    }

    public static NoAccessibleConstructorFoundException forClass(Class<?> clazz) {
        return new NoAccessibleConstructorFoundException(MESSAGE.formatted(clazz.getName()));
    }
}
