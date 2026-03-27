package com.yawl.http;

import com.yawl.annotations.RequestBody;
import com.yawl.http.model.Route;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.lang.reflect.Parameter;

public class RequestBodyArgumentResolver implements HttpMethodArgumentResolver {
    private static final Logger log = LoggerFactory.getLogger(RequestBodyArgumentResolver.class);

    private final JsonMapper jsonMapper;

    public RequestBodyArgumentResolver(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public boolean supports(Parameter parameter) {
        return parameter.isAnnotationPresent(RequestBody.class);
    }

    @Override
    public Object resolve(HttpServletRequest request, Route route, Parameter parameter) {
        try {
            return jsonMapper.readValue(request.getReader(), parameter.getType());
        } catch (IOException ex) {
            log.error("Unable to parse body", ex);
            throw new RuntimeException("Unable to parse body", ex);
        }
    }
}
