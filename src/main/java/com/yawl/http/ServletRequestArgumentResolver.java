package com.yawl.http;

import com.yawl.http.model.Route;
import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Parameter;
import java.util.List;

/**
 * Delegates parameter resolution to the first matching {@link HttpMethodArgumentResolver}.
 */
public class ServletRequestArgumentResolver {
    private final List<HttpMethodArgumentResolver> resolvers;

    /**
     * Creates a new resolver with the given list of argument resolvers.
     *
     * @param resolvers the available argument resolvers
     */
    public ServletRequestArgumentResolver(List<HttpMethodArgumentResolver> resolvers) {
        this.resolvers = resolvers;
    }

    /**
     * Resolves the value for the given handler method parameter.
     *
     * @param request   the HTTP request
     * @param route     the matched route
     * @param parameter the method parameter
     * @return the resolved value, or {@code null} if no resolver supports the parameter
     */
    public Object resolveParameter(HttpServletRequest request, Route route, Parameter parameter) {
        return resolvers.stream()
                .filter(resolver -> resolver.supports(parameter))
                .map(resolver -> resolver.resolve(request, route, parameter))
                .findFirst().orElse(null);
    }
}
