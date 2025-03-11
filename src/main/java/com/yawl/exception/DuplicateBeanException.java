package com.yawl.exception;

public class DuplicateBeanException extends RuntimeException {
    private static final String MESSAGE = "Bean with name %s already exists";
    private static final String MESSAGE_MULTIPLE_BEANS_FOUND = "Bean with signature %s is not singular";

    private DuplicateBeanException(String message) {
        super(message);
    }

    public static DuplicateBeanException forBeanName(String beanName) {
        return new DuplicateBeanException(MESSAGE.formatted(beanName));
    }

    public static DuplicateBeanException forClass(Class<?> clazz) {
        return new DuplicateBeanException(MESSAGE_MULTIPLE_BEANS_FOUND.formatted(clazz));
    }
}
