package com.yawl.exception;

/**
 * Thrown when a bean cannot be instantiated or initialized.
 */
public class UnableToInitializeBeanException extends RuntimeException {
    private static final String MESSAGE = "Unable to initialize bean %s";

    private UnableToInitializeBeanException(String beanName) {
        super(MESSAGE.formatted(beanName));
    }

    /**
     * Creates an exception for a bean that failed to initialize.
     *
     * @param clazz the bean class
     * @return a new exception instance
     */
    public static UnableToInitializeBeanException forClass(Class<?> clazz) {
        return new UnableToInitializeBeanException(clazz.getSimpleName());
    }
}
