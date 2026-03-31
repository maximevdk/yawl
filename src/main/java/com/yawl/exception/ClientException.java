package com.yawl.exception;

/**
 * Base exception for client-side errors that result in HTTP 4xx responses.
 */
abstract class ClientException extends RuntimeException {
    /**
     * Creates a new client exception with the given message.
     *
     * @param message the detail message
     */
    public ClientException(String message) {
        super(message);
    }
}
