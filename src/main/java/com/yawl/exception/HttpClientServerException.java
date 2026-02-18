package com.yawl.exception;

public class HttpClientServerException extends RuntimeException {
    public HttpClientServerException(int statusCode, String reason) {
        super("Request failed with status code: %s and reason %s:".formatted(statusCode, reason));
    }
}
