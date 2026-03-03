package com.yawl.http.client;

import com.yawl.util.StringUtils;

record Parameter(String name, Object value, In in) {

    public static Parameter path(String name, Object value) {
        return new Parameter(name, value, In.PATH);
    }

    public static Parameter query(String name, Object value) {
        return new Parameter(name, value, In.QUERY);
    }

    public String valueAsString() {
        return StringUtils.toString(value);
    }

    public boolean isPathParam() {
        return In.PATH == in;
    }

    public boolean isQueryParam() {
        return In.QUERY == in;
    }

    enum In {
        PATH,
        QUERY
    }
}
