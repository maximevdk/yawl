package com.yawl.model;

public record MediaType(String value) {
    public static final String APPLICATION_JSON_VALUE = "application/json";
    public static final MediaType APPLICATION_JSON = new MediaType(APPLICATION_JSON_VALUE);

    public static MediaType of(String value) {
        return new MediaType(value);
    }
}
