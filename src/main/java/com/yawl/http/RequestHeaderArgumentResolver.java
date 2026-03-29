package com.yawl.http;

import com.yawl.annotations.RequestHeader;
import com.yawl.exception.MissingRequiredHeaderException;
import com.yawl.http.model.Route;
import com.yawl.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Parameter;

public class RequestHeaderArgumentResolver implements HttpMethodArgumentResolver {
    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(RequestHeader.class);
    }

    @Override
    public Object resolve(HttpServletRequest request, Route route, Parameter parameter) {
        var requestHeader = parameter.getAnnotation(RequestHeader.class);
        var headerName = requestHeader.name() != null ? requestHeader.name() : parameter.getName();

        var values = StringUtils.parse(request.getHeader(headerName), parameter.getParameterizedType());
        if (values == null && requestHeader.required()) {
            throw MissingRequiredHeaderException.of(headerName);
        }

        return values;
    }
}
