package com.yawl.model;

public record ServerError(HttpStatus status, String message) {

    public static ServerError internal(String message) {
        return new ServerError(HttpStatus.ERROR, message);
    }

    public static ServerError internal() {
        return new ServerError(HttpStatus.ERROR, "Unexpected error has occurred");
    }
}
