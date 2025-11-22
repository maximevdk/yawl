package com.yawl.model;

public record RequestParameter(In in, String name, Class<?> type, boolean required) {


    public static RequestParameter query(String name, Class<?> type, boolean required) {
        return new RequestParameter(In.QUERY, name, type, required);
    }

    public static RequestParameter path(String name, Class<?> type) {
        return new RequestParameter(In.PATH, name, type, true);
    }

    enum In {
        PATH,
        QUERY,
        HEADER
    }
}
