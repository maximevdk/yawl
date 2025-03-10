package com.yawl.model;

public record Destination(Class<?> controller, String methodName, String mediaType) {
}
