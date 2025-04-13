package com.yawl.model;

public record RequestDestination(Class<?> controller, RequestMethod method) {

    public int statusCode() {
        return method.status().getCode();
    }

    public String produces() {
        return method.produces().value();
    }
}
