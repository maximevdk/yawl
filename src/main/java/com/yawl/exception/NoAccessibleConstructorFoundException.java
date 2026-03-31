package com.yawl.exception;

import java.util.Arrays;

/**
 * Thrown when no accessible constructor matching the required arguments is found for a bean class.
 */
public class NoAccessibleConstructorFoundException extends RuntimeException {
    private static final String MESSAGE = "No accessible constructor found for class [%s] and arguments [%s].";

    private NoAccessibleConstructorFoundException(String message) {
        super(message);
    }

    /**
     * Creates an exception for a class with no matching accessible constructor.
     *
     * @param clazz the bean class
     * @param args  the constructor arguments that were attempted
     * @return a new exception instance
     */
    public static NoAccessibleConstructorFoundException of(Class<?> clazz, Object... args) {
        return new NoAccessibleConstructorFoundException(MESSAGE.formatted(clazz.getName(), Arrays.toString(args)));
    }
}
