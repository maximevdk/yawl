package com.yawl.http.model;

import jakarta.annotation.Nonnull;

import java.util.List;
import java.util.Map;

/**
 * Represents an HTTP route defined by an HTTP method and a URL path pattern.
 *
 * @param method  the HTTP method
 * @param pattern the path pattern
 */
public record Route(@Nonnull HttpMethod method, @Nonnull PathPattern pattern) implements Comparable<Route> {
    /**
     * Creates a route for the given HTTP method and path segments.
     *
     * @param method the HTTP method
     * @param paths  the path segments to join
     * @return a new route
     */
    public static Route of(HttpMethod method, String... paths) {
        var path = String.join("/", paths);
        return new Route(method, PathPattern.parse(path));
    }

    /**
     * Creates a GET route from the given path segments.
     *
     * @param paths the path segments
     * @return a new GET route
     */
    public static Route get(String... paths) {
        return new Route(HttpMethod.GET, PathPattern.parse(String.join("/", paths)));
    }

    /**
     * Creates a POST route from the given path segments.
     *
     * @param paths the path segments
     * @return a new POST route
     */
    public static Route post(String... paths) {
        return new Route(HttpMethod.POST, PathPattern.parse(String.join("/", paths)));
    }

    /**
     * Creates a PUT route from the given path segments.
     *
     * @param paths the path segments
     * @return a new PUT route
     */
    public static Route put(String... paths) {
        return new Route(HttpMethod.PUT, PathPattern.parse(String.join("/", paths)));
    }

    /**
     * Creates a DELETE route from the given path segments.
     *
     * @param paths the path segments
     * @return a new DELETE route
     */
    public static Route delete(String... paths) {
        return new Route(HttpMethod.DELETE, PathPattern.parse(String.join("/", paths)));
    }

    /**
     * Returns the names of all path parameters in this route's pattern.
     *
     * @return a list of path parameter names
     */
    public List<String> pathParamNames() {
        return pattern.captureNames();
    }

    /**
     * Extracts path variable values from the given request path.
     *
     * @param requestPath the request path
     * @return a map of variable names to values
     */
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

    @Override
    public int compareTo(Route o) {
        return pattern.literalCount() - o.pattern().literalCount();
    }
}
