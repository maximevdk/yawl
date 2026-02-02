package com.yawl.exception;

import java.util.Map;

public class NotABeanException extends RuntimeException {
    private static final String MESSAGE_BY_TYPE = "Class %s is not a registered bean";

    private NotABeanException(String message) {
        super(message);
    }

    public static NotABeanException forClass(Class<?> clazz) {
        return new NotABeanException(MESSAGE_BY_TYPE.formatted(clazz));
    }

}
