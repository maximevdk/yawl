package com.yawl.http;

import com.yawl.annotations.QueryParam;
import com.yawl.common.util.StringUtils;
import com.yawl.exception.MissingRequiredParameterException;
import com.yawl.http.model.Route;
import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Optional;

/**
 * Resolves handler method parameters annotated with {@link com.yawl.annotations.QueryParam} from query string parameters.
 */
public class QueryParamArgumentResolver implements HttpMethodArgumentResolver {
    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(QueryParam.class);
    }

    @Override
    public Object resolve(HttpServletRequest request, Route route, Parameter parameter) {
        var queryParam = parameter.getAnnotation(QueryParam.class);
        var parameterName = queryParam.name() != null ? queryParam.name() : parameter.getName();

        var values = Optional.ofNullable(request.getParameterValues(parameterName))
                .stream()
                .flatMap(Arrays::stream)
                .filter(StringUtils::hasText)
                .toArray(String[]::new);
        var value = StringUtils.parse(values, parameter.getParameterizedType());
        if (value == null && queryParam.required()) {
            throw MissingRequiredParameterException.of(parameterName);
        }

        return value;
    }
}
