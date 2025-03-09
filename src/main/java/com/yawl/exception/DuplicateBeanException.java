package com.yawl.exception;

public class DuplicateBeanException extends RuntimeException {
    private static final String MESSAGE = "Bean with name %s already exists";

    private DuplicateBeanException(String message) {
        super(message);
    }

    public static DuplicateBeanException forBeanName(String beanName) {
        return new DuplicateBeanException(MESSAGE.formatted(beanName));
    }
}
