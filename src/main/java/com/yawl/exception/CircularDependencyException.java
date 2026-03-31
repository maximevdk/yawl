package com.yawl.exception;

/**
 * Thrown when a circular dependency is detected in the bean dependency graph.
 */
public class CircularDependencyException extends RuntimeException {
    /**
     * Creates a new exception with the given message.
     *
     * @param message a description of the circular dependency
     */
    public CircularDependencyException(String message) {
        super(message);
    }
}
