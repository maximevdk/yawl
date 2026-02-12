package com.yawl.http.model;

public record ContentType(String value) {
    public static final String APPLICATION_JSON_VALUE = "application/json";
    public static final ContentType APPLICATION_JSON = new ContentType(APPLICATION_JSON_VALUE);

    public static ContentType of(String value) {
        return new ContentType(value);
    }
}
