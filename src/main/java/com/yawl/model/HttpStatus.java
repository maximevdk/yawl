package com.yawl.model;

import jakarta.servlet.http.HttpServletResponse;

public enum HttpStatus {
    OK(HttpServletResponse.SC_OK),
    STARTING(HttpServletResponse.SC_SERVICE_UNAVAILABLE),
    ERROR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);


    private final int code;

    HttpStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
