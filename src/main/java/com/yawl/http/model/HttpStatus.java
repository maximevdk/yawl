package com.yawl.http.model;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Support for RFC https://datatracker.ietf.org/doc/html/rfc2616#section-10.2
 */
public enum HttpStatus {
    OK(HttpServletResponse.SC_OK, Series.SUCCESSFUL),
    CREATED(HttpServletResponse.SC_CREATED, Series.SUCCESSFUL),
    ACCEPTED(HttpServletResponse.SC_ACCEPTED, Series.SUCCESSFUL),
    NO_CONTENT(HttpServletResponse.SC_NO_CONTENT, Series.SUCCESSFUL),
    BAD_REQUEST(HttpServletResponse.SC_BAD_REQUEST, Series.CLIENT_ERROR),
    NOT_FOUND(HttpServletResponse.SC_NOT_FOUND, Series.CLIENT_ERROR),
    ERROR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Series.SERVER_ERROR),
    STARTING(HttpServletResponse.SC_SERVICE_UNAVAILABLE, Series.SERVER_ERROR);

    private final int code;
    private final Series series;

    HttpStatus(int code, Series series) {
        this.code = code;
        this.series = series;
    }

    public int getCode() {
        return code;
    }

    public boolean is2xxSuccessful() {
        return series == Series.SUCCESSFUL;
    }

    public static HttpStatus of(int code) {
        if (code < 100 || code > 999) {
            throw new IllegalArgumentException("HttpStatus code should be positive and exist out of 3 digits");
        }

        for (HttpStatus httpStatus : HttpStatus.values()) {
            if (httpStatus.code == code) {
                return httpStatus;
            }
        }

        throw new IllegalArgumentException("HttpStatus code " + code + " not found");
    }

    public enum Series {
        INFORMATIONAL, //1xx
        SUCCESSFUL, //2xx
        REDIRECTION, //3xx
        CLIENT_ERROR,  //4xx
        SERVER_ERROR //5xx
    }
}
