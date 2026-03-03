package com.yawl.http.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

public sealed interface HttpResponse<T> permits HttpResponse.ErrorResponse, HttpResponse.NoContent, HttpResponse.Ok {
    @JsonProperty
    HttpStatus status();

    T body();

    sealed interface ErrorResponse extends HttpResponse<String> permits ErrorResponse.BadRequest, ErrorResponse.Error, ErrorResponse.NotFound {
        String reason();

        default String body() {
            return reason();
        }

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

    record Ok<T>(@JsonValue T body, HttpStatus status) implements HttpResponse<T> {
    }

    record NoContent(HttpStatus status) implements HttpResponse<Void> {
        @Override
        public Void body() {
            return null;
        }
    }

    static <T> HttpResponse<T> ok(T body, HttpStatus status) {
        return new Ok<>(body, status);
    }

    static <T> HttpResponse<T> ok(T body) {
        return new Ok<>(body, HttpStatus.OK);
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
