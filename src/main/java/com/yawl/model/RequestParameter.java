package com.yawl.model;

public record RequestParameter(String name, Class<?> type, boolean required) {
}
