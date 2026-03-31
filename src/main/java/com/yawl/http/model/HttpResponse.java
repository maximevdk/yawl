package com.yawl.http.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Sealed hierarchy representing HTTP responses with a status and optional body.
 *
 * @param <T> the body type
 */
public sealed interface HttpResponse<T> permits HttpResponse.ErrorResponse, HttpResponse.NoContent, HttpResponse.Ok {
    /**
     * Returns the HTTP status of this response.
     *
     * @return the HTTP status
     */
    @JsonProperty
    HttpStatus status();

    /**
     * Returns the response body.
     *
     * @return the body, or {@code null} for no-content responses
     */
    T body();

    /**
     * Represents an error HTTP response with a reason message.
     */
    sealed interface ErrorResponse extends HttpResponse<String> permits ErrorResponse.BadRequest, ErrorResponse.Error, ErrorResponse.NotFound {
        /**
         * Returns the error reason message.
         *
         * @return the reason
         */
        String reason();

        default String body() {
            return reason();
        }

        /**
         * Represents a 500 Internal Server Error response.
         *
         * @param reason the error description
         */
        record Error(String reason) implements ErrorResponse {
            @Override
            public HttpStatus status() {
                return HttpStatus.ERROR;
            }
        }

        /**
         * Represents a 404 Not Found response.
         *
         * @param reason the error description
         */
        record NotFound(String reason) implements ErrorResponse {

            @Override
            public HttpStatus status() {
                return HttpStatus.NOT_FOUND;
            }
        }

        /**
         * Represents a 400 Bad Request response.
         *
         * @param reason the error description
         */
        record BadRequest(String reason) implements ErrorResponse {
            @Override
            public HttpStatus status() {
                return HttpStatus.BAD_REQUEST;
            }
        }
    }

    /**
     * Represents a successful HTTP response with a body.
     *
     * @param body   the response body
     * @param status the HTTP status
     * @param <T>    the body type
     */
    record Ok<T>(@JsonValue T body, HttpStatus status) implements HttpResponse<T> {
    }

    /**
     * Represents an HTTP response with no body.
     *
     * @param status the HTTP status
     */
    record NoContent(HttpStatus status) implements HttpResponse<Void> {
        @Override
        public Void body() {
            return null;
        }
    }

    /**
     * Creates a successful response with the given body and status.
     *
     * @param body   the response body
     * @param status the HTTP status
     * @param <T>    the body type
     * @return a new OK response
     */
    static <T> HttpResponse<T> ok(T body, HttpStatus status) {
        return new Ok<>(body, status);
    }

    /**
     * Creates a successful response with HTTP 200 OK.
     *
     * @param body the response body
     * @param <T>  the body type
     * @return a new OK response
     */
    static <T> HttpResponse<T> ok(T body) {
        return new Ok<>(body, HttpStatus.OK);
    }

    /**
     * Creates a no-content response with the given status.
     *
     * @param status the HTTP status
     * @return a new no-content response
     */
    static NoContent noContent(HttpStatus status) {
        return new NoContent(status);
    }

    /**
     * Creates a 404 Not Found response.
     *
     * @param description the error description
     * @return a new not-found response
     */
    static HttpResponse<String> notFound(String description) {
        return new ErrorResponse.NotFound(description);
    }

    /**
     * Creates a 500 Internal Server Error response with a default message.
     *
     * @return a new error response
     */
    static HttpResponse<String> internal() {
        return new ErrorResponse.Error("An unexpected error has occurred");
    }

    /**
     * Creates a 500 Internal Server Error response with the given description.
     *
     * @param description the error description
     * @return a new error response
     */
    static HttpResponse<String> internal(String description) {
        return new ErrorResponse.Error(description);
    }

    /**
     * Creates a 400 Bad Request response.
     *
     * @param reason the error reason
     * @return a new bad-request response
     */
    static HttpResponse<String> badRequest(String reason) {
        return new ErrorResponse.BadRequest(reason);
    }
}
