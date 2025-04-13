package com.yawl.exception;

public class UnableToInitializeBeanException extends RuntimeException {
    private static final String MESSAGE = "Unable to initialize bean %s";

    private UnableToInitializeBeanException(String beanName) {
        super(MESSAGE.formatted(beanName));
    }

    public static UnableToInitializeBeanException forClass(Class<?> clazz) {
        return new UnableToInitializeBeanException(clazz.getSimpleName());
    }
}
