package com.yawl.http;

import com.yawl.http.model.Route;
import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Parameter;
import java.util.List;

public class ServletRequestArgumentResolver {
    private final List<HttpMethodArgumentResolver> resolvers;

    public ServletRequestArgumentResolver(List<HttpMethodArgumentResolver> resolvers) {
        this.resolvers = resolvers;
    }

    public Object resolveParameter(HttpServletRequest request, Route route, Parameter parameter) {
        return resolvers.stream()
                .filter(resolver -> resolver.supports(parameter))
                .map(resolver -> resolver.resolve(request, route, parameter))
                .findFirst().orElse(null);
    }
}
