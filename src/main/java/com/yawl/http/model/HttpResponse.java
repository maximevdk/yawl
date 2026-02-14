package com.yawl.http.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public sealed interface HttpResponse permits HttpResponse.ErrorResponse, HttpResponse.NoContent, HttpResponse.Ok {
    @JsonProperty
    HttpStatus status();


    sealed interface ErrorResponse extends HttpResponse permits ErrorResponse.BadRequest, ErrorResponse.Error, ErrorResponse.NotFound {
        String reason();

        record Error(String reason) implements ErrorResponse {
            @Override
            public HttpStatus status() {
                return HttpStatus.ERROR;
            }
        }

        record NotFound(String reason) implements ErrorResponse {

            @Override
            public HttpStatus status() {
                return HttpStatus.NOT_FOUND;
            }
        }

        record BadRequest(String reason) implements ErrorResponse {
            @Override
            public HttpStatus status() {
                return HttpStatus.BAD_REQUEST;
            }
        }
    }

    record Ok<T>(@JsonValue T body, HttpStatus status) implements HttpResponse {
    }

    record NoContent(HttpStatus status) implements HttpResponse {
    }

    static <T> HttpResponse ok(T body, HttpStatus status) {
        return new Ok<>(body, status);
    }

    static NoContent noContent(HttpStatus status) {
        return new NoContent(status);
    }

    static ErrorResponse.NotFound notFound(String description) {
        return new ErrorResponse.NotFound(description);
    }

    static ErrorResponse.Error internal() {
        return new ErrorResponse.Error("An unexpected error has occurred");
    }

    static ErrorResponse.Error internal(String description) {
        return new ErrorResponse.Error(description);
    }


    static ErrorResponse.BadRequest badRequest(String reason) {
        return new ErrorResponse.BadRequest(reason);
    }
}
