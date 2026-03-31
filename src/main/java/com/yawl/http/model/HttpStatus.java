package com.yawl.http.model;

import jakarta.servlet.http.HttpServletResponse;

/**
 * Support for RFC https://datatracker.ietf.org/doc/html/rfc2616#section-10.2
 */
public enum HttpStatus {
    /** {@code 200 OK}. */
    OK(HttpServletResponse.SC_OK, Series.SUCCESSFUL),
    /** {@code 201 Created}. */
    CREATED(HttpServletResponse.SC_CREATED, Series.SUCCESSFUL),
    /** {@code 202 Accepted}. */
    ACCEPTED(HttpServletResponse.SC_ACCEPTED, Series.SUCCESSFUL),
    /** {@code 204 No Content}. */
    NO_CONTENT(HttpServletResponse.SC_NO_CONTENT, Series.SUCCESSFUL),
    /** {@code 400 Bad Request}. */
    BAD_REQUEST(HttpServletResponse.SC_BAD_REQUEST, Series.CLIENT_ERROR),
    /** {@code 404 Not Found}. */
    NOT_FOUND(HttpServletResponse.SC_NOT_FOUND, Series.CLIENT_ERROR),
    /** {@code 406 Not Acceptable}. */
    NOT_ACCEPTABLE(HttpServletResponse.SC_NOT_ACCEPTABLE, Series.CLIENT_ERROR),
    /** {@code 500 Internal Server Error}. */
    ERROR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, Series.SERVER_ERROR),
    /** {@code 503 Service Unavailable}. */
    STARTING(HttpServletResponse.SC_SERVICE_UNAVAILABLE, Series.SERVER_ERROR);

    private final int code;
    private final Series series;

    HttpStatus(int code, Series series) {
        this.code = code;
        this.series = series;
    }

    /**
     * Returns the numeric HTTP status code.
     *
     * @return the status code
     */
    public int getCode() {
        return code;
    }

    /**
     * Returns whether this status is in the 2xx Successful series.
     *
     * @return {@code true} if the status is successful
     */
    public boolean is2xxSuccessful() {
        return series == Series.SUCCESSFUL;
    }

    /**
     * Returns the {@code HttpStatus} matching the given numeric code.
     *
     * @param code the HTTP status code
     * @return the matching status
     * @throws IllegalArgumentException if no matching status is found
     */
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

    /**
     * HTTP status code series grouping.
     */
    public enum Series {
        /** 1xx Informational. */
        INFORMATIONAL,
        /** 2xx Successful. */
        SUCCESSFUL,
        /** 3xx Redirection. */
        REDIRECTION,
        /** 4xx Client Error. */
        CLIENT_ERROR,
        /** 5xx Server Error. */
        SERVER_ERROR
    }
}
