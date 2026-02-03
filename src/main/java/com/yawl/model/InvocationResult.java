package com.yawl.model;

import java.util.Optional;

public record InvocationResult<T>(boolean success, T result) {

    public static <T> InvocationResult<T> success(T result) {
        return new InvocationResult<>(true, result);
    }

    public static InvocationResult<String> failed(String reason) {
        return new InvocationResult<>(false, reason);
    }

    public Optional<T> resultAsOptional() {
        return Optional.ofNullable(result);
    }
}
