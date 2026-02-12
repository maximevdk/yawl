package com.yawl.model;

import java.util.Optional;

public record Invocation<T>(boolean success, T result) {

    public static <T> Invocation<T> success(T result) {
        return new Invocation<>(true, result);
    }

    public static Invocation<String> failed(String reason) {
        return new Invocation<>(false, reason);
    }

    public Optional<T> resultAsOptional() {
        return Optional.ofNullable(result);
    }
}
