package com.yawl.exception;

/**
 * Thrown when a bean with a duplicate name or non-singular type is registered in the application context.
 */
public class DuplicateBeanException extends RuntimeException {
    private static final String MESSAGE = "Bean with name %s already exists";
    private static final String MESSAGE_MULTIPLE_BEANS_FOUND = "Bean with signature %s is not singular";

    private DuplicateBeanException(String message) {
        super(message);
    }

    /**
     * Creates an exception for a duplicate bean name.
     *
     * @param beanName the duplicate bean name
     * @return a new exception instance
     */
    public static DuplicateBeanException forBeanName(String beanName) {
        return new DuplicateBeanException(MESSAGE.formatted(beanName));
    }

    /**
     * Creates an exception for a non-singular bean type.
     *
     * @param clazz the bean type that matched multiple registrations
     * @return a new exception instance
     */
    public static DuplicateBeanException forClass(Class<?> clazz) {
        return new DuplicateBeanException(MESSAGE_MULTIPLE_BEANS_FOUND.formatted(clazz));
    }
}
