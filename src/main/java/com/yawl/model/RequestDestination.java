package com.yawl.model;

public record RequestDestination(Class<?> controller, RequestMethod method) {
}
