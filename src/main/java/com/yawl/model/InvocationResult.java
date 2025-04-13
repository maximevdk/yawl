package com.yawl.model;

import java.util.Optional;

public record InvocationResult(boolean success, Object result) {

    public static InvocationResult success(Object result) {
        return new InvocationResult(true, result);
    }

    public static InvocationResult failed(String reason) {
        return new InvocationResult(false, reason);
    }


    public Optional<Object> resultAsOptional() {
        return Optional.ofNullable(result);
    }
}
