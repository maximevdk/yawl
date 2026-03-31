package com.yawl.exception;

/**
 * Thrown when an HTTP client receives a non-successful response from the remote server.
 */
public class HttpClientServerException extends RuntimeException {
    /**
     * Creates a new exception with the given status code and reason.
     *
     * @param statusCode the HTTP status code
     * @param reason     the reason phrase
     */
    public HttpClientServerException(int statusCode, String reason) {
        super("Request failed with status code: %s and reason %s:".formatted(statusCode, reason));
    }
}
