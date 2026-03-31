package com.yawl.exception;

import java.util.Arrays;

public class NoAccessibleConstructorFoundException extends RuntimeException {
    private static final String MESSAGE = "No accessible constructor found for class [%s] and arguments [%s].";

    private NoAccessibleConstructorFoundException(String message) {
        super(message);
    }

    public static NoAccessibleConstructorFoundException of(Class<?> clazz, Object... args) {
        return new NoAccessibleConstructorFoundException(MESSAGE.formatted(clazz.getName(), Arrays.toString(args)));
    }
}
