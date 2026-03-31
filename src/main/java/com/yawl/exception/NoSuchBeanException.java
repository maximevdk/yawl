package com.yawl.exception;

/**
 * Thrown when a requested bean cannot be found in the application context.
 */
public class NoSuchBeanException extends RuntimeException {
    private static final String MESSAGE = "No bean of type %s found";
    private static final String MESSAGE_BY_NAME = "No bean with name %s found";
    private static final String MESSAGE_DEPENDENCY_FOR_TYPE = "No bean of type %s found which %s depends on";

    private NoSuchBeanException(String message) {
        super(message);
    }

    /**
     * Creates an exception for a missing bean of the given type.
     *
     * @param clazz the bean type
     * @return a new exception instance
     */
    public static NoSuchBeanException forClass(Class<?> clazz) {
        return new NoSuchBeanException(MESSAGE.formatted(clazz));
    }

    /**
     * Creates an exception for a missing dependency.
     *
     * @param clazz      the bean that requires the dependency
     * @param dependency the missing dependency type
     * @return a new exception instance
     */
    public static NoSuchBeanException forDependency(Class<?> clazz, Class<?> dependency) {
        return new NoSuchBeanException(MESSAGE_DEPENDENCY_FOR_TYPE.formatted(dependency, clazz));
    }

    /**
     * Creates an exception for a missing bean with the given name.
     *
     * @param name the bean name
     * @return a new exception instance
     */
    public static NoSuchBeanException forName(String name) {
        return new NoSuchBeanException(MESSAGE_BY_NAME.formatted(name));
    }
}
