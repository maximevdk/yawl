package com.yawl.model;

import jakarta.servlet.http.HttpServletResponse;

public enum HttpStatus {
    OK(HttpServletResponse.SC_OK),
    ACCEPTED(HttpServletResponse.SC_ACCEPTED),
    BAD_REQUEST(HttpServletResponse.SC_BAD_REQUEST),
    NOT_FOUND(HttpServletResponse.SC_NOT_FOUND),
    ERROR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR),
    STARTING(HttpServletResponse.SC_SERVICE_UNAVAILABLE);

    private final int code;

    HttpStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
