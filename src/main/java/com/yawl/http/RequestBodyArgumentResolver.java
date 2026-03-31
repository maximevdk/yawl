package com.yawl.http;

import com.yawl.annotations.RequestBody;
import com.yawl.http.model.Route;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

import java.lang.reflect.Parameter;

/**
 * Resolves handler method parameters annotated with {@link com.yawl.annotations.RequestBody} by deserializing the request body.
 */
public class RequestBodyArgumentResolver implements HttpMethodArgumentResolver {
    private static final Logger log = LoggerFactory.getLogger(RequestBodyArgumentResolver.class);

    //TODO: body can be more than just JSON, like XML or anything else, we should allow for a reader instead of limiting us to jsonMapper
    private final JsonMapper jsonMapper;

    /**
     * Creates a new resolver with the given JSON mapper.
     *
     * @param jsonMapper the mapper used to deserialize request bodies
     */
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
        } catch (Exception ex) {
            log.error("Unable to parse body", ex);
            throw new RuntimeException("Unable to parse body", ex);
        }
    }
}
