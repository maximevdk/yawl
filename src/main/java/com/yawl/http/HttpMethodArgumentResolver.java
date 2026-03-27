package com.yawl.http;

import com.yawl.http.model.Route;
import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Parameter;

public interface HttpMethodArgumentResolver {
    boolean supports(Parameter parameter);

    Object resolve(HttpServletRequest request, Route route, Parameter parameter);
}
