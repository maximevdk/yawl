package com.yawl.http;

import com.yawl.annotations.QueryParam;
import com.yawl.exception.MissingRequiredParameterException;
import com.yawl.http.model.Route;
import com.yawl.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Parameter;

public class QueryParamArgumentResolver implements HttpMethodArgumentResolver {
    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(QueryParam.class);
    }

    @Override
    public Object resolve(HttpServletRequest request, Route route, Parameter parameter) {
        var queryParam = parameter.getAnnotation(QueryParam.class);
        var parameterName = queryParam.name() != null ? queryParam.name() : parameter.getName();

        var value = StringUtils.parse(request.getParameterValues(parameterName), parameter.getParameterizedType());
        if (value == null && queryParam.required()) {
            throw MissingRequiredParameterException.of(parameterName);
        }

        return value;
    }
}
