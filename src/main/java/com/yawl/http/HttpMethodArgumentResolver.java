package com.yawl.http;

import com.yawl.http.model.Route;
import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Parameter;

/**
 * Strategy interface for resolving handler method parameters from an HTTP servlet request.
 */
public interface HttpMethodArgumentResolver {
    /**
     * Returns whether this resolver supports the given parameter.
     *
     * @param parameter the method parameter to check
     * @return {@code true} if this resolver can handle the parameter
     */
    boolean supports(Parameter parameter);

    /**
     * Resolves the value for the given parameter from the HTTP request.
     *
     * @param request   the current HTTP request
     * @param route     the matched route
     * @param parameter the parameter to resolve
     * @return the resolved parameter value
     */
    Object resolve(HttpServletRequest request, Route route, Parameter parameter);
}
