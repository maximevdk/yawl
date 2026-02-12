package com.yawl.http.model;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

public record Route(@Nonnull HttpMethod method, @Nonnull PathPattern pattern) {
    public static Route of(HttpMethod method, String... paths) {
        var path = String.join("/", paths);
        return new Route(method, PathPattern.parse(path));
    }

    public static Route get(String... paths) {
        return new Route(HttpMethod.GET, PathPattern.parse(String.join("/", paths)));
    }

    public static Route post(String... paths) {
        return new Route(HttpMethod.POST, PathPattern.parse(String.join("/", paths)));
    }

    public List<String> pathParamNames() {
        return pattern.captureNames();
    }

    public Map<String, String> extractVariables(String requestPath) {
        return pattern.extractPathVariables(requestPath);
    }

    /**
     * Checks if the two routes are the same.
     * If the two paths are not a direct match, it will try to verify if there are path params which causes that
     * the routes don't match to each other 100%.
     *
     * @param method      the method object with which to compare.
     * @param requestPath the requestPath with which to compare.
     * @return true if the request path and method match this route
     */
    public boolean matches(HttpMethod method, String requestPath) {
        return this.method == method && pattern.matches(requestPath);
    }
}
