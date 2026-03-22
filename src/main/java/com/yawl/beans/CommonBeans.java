package com.yawl.beans;

import com.yawl.exception.BadRequestExceptionResolver;
import com.yawl.exception.ExceptionResolver;
import com.yawl.exception.FallbackExceptionResolver;
import com.yawl.exception.RouteNotFoundExceptionResolver;

public final class CommonBeans {
    public static final String APPLICATION_PROPERTIES_NAME = "applicationProperties";
    public static final String JSON_MAPPER_NAME = "jsonMapper";
    public static final String YAML_MAPPER_NAME = "yamlMapper";
    public static final String EVENT_PUBLISHER_NAME = "eventPublisher";
    public static final String WEB_SERVER_NAME = "webserver";

    public static void registerExceptionResolvers(ApplicationContext applicationContext) {
        if (!applicationContext.containsBeanOfType(RouteNotFoundExceptionResolver.class)) {
            applicationContext.register("routeNotFoundExceptionResolver", new RouteNotFoundExceptionResolver(), ExceptionResolver.class);
        }

        if (!applicationContext.containsBeanOfType(BadRequestExceptionResolver.class)) {
            applicationContext.register("badRequestExceptionResolver", new BadRequestExceptionResolver(), ExceptionResolver.class);
        }

        if (!applicationContext.containsBeanOfType(FallbackExceptionResolver.class)) {
            applicationContext.register("FallbackExceptionResolver", new FallbackExceptionResolver(), ExceptionResolver.class);
        }
    }
}
