package com.yawl;

import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

import java.util.Set;

public class DefaultServletContainerInitializer implements ServletContainerInitializer {
    private static final Logger log = LoggerFactory.getLogger(DefaultServletContainerInitializer.class);

    private final ApplicationProperties.Application properties;
    private final JsonMapper jsonMapper;

    public DefaultServletContainerInitializer(ApplicationProperties.Application properties, JsonMapper jsonMapper) {
        this.properties = properties;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext context) throws ServletException {
        log.info("Registering default servlets");

        var dispatcherServlet = context.addServlet("dispatcherServlet", new DispatcherServlet(jsonMapper));
        dispatcherServlet.addMapping(properties.web().config().contextPath());

        if (properties.management().managementEndpointEnabled()) {
            var managementServlet = context.addServlet("managementServlet", new ManagementServlet(jsonMapper, properties));
            managementServlet.addMapping(properties.management().endpoint().path());
        }
    }
}
