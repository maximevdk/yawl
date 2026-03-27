package com.yawl.http;

import com.yawl.annotations.PathParam;
import com.yawl.common.util.StringUtils;
import com.yawl.exception.MissingPathParameterException;
import com.yawl.http.model.Route;
import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Parameter;

public class PathParamArgumentResolver implements HttpMethodArgumentResolver {
    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(PathParam.class);
    }

    @Override
    public Object resolve(HttpServletRequest request, Route route, Parameter parameter) {
        var pathParam = parameter.getAnnotation(PathParam.class);
        var parameterName = pathParam.name() != null ? pathParam.name() : parameter.getName();
        var pathParams = route.extractVariables(request.getRequestURI());

        var value = StringUtils.parse(new String[]{pathParams.get(parameterName)}, parameter.getParameterizedType());

        if (value == null) {
            throw MissingPathParameterException.of(route, parameterName);
        }

        return value;
    }
}
