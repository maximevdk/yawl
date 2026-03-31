package com.yawl.exception;

/**
 * Thrown when a class that is not registered as a bean is used where a bean is expected.
 */
public class NotABeanException extends RuntimeException {
    private static final String MESSAGE_BY_TYPE = "Class %s is not a registered bean";

    private NotABeanException(String message) {
        super(message);
    }

    /**
     * Creates an exception for a class that is not a registered bean.
     *
     * @param clazz the class
     * @return a new exception instance
     */
    public static NotABeanException forClass(Class<?> clazz) {
        return new NotABeanException(MESSAGE_BY_TYPE.formatted(clazz));
    }

}
