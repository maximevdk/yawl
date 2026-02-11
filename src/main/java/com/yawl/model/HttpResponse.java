package com.yawl.model;

public sealed interface HttpResponse permits HttpResponse.BadRequest, HttpResponse.Error, HttpResponse.NotFound {
    HttpStatus status();

    String description();

    record Error(String description) implements HttpResponse {
        @Override
        public HttpStatus status() {
            return HttpStatus.ERROR;
        }
    }

    record NotFound(String description) implements HttpResponse {

        @Override
        public HttpStatus status() {
            return HttpStatus.NOT_FOUND;
        }
    }

    record BadRequest(String description) implements HttpResponse {
        @Override
        public HttpStatus status() {
            return HttpStatus.BAD_REQUEST;
        }
    }

    static BadRequest badRequest(String description) {
        return new BadRequest(description);
    }

    static NotFound notFound(String description) {
        return new NotFound(description);
    }

    static Error internal() {
        return new Error("An unexpected error has occurred");
    }

    static Error internal(String description) {
        return new Error(description);
    }

}
