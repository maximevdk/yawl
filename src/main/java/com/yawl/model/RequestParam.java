package com.yawl.model;

import java.util.regex.Pattern;

public sealed interface RequestParam permits RequestParam.QueryRequestParameter, RequestParam.PathRequestParameter {
    boolean required();

    String name();

    Class<?> type();


    static QueryRequestParameter query(String name, Class<?> type, boolean required) {
        return new QueryRequestParameter(name, type, required);
    }

    static PathRequestParameter path(String name, Class<?> type, Pattern capturePattern) {
        return new PathRequestParameter(name, type, capturePattern);
    }

    record QueryRequestParameter(String name, Class<?> type, boolean required) implements RequestParam {
    }

    record PathRequestParameter(String name, Class<?> type, Pattern capturePattern) implements RequestParam {
        @Override
        public boolean required() {
            return true;
        }
    }
}
