package com.yawl;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.yawl.beans.BeanRegistry;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class DefaultServletContainerInitializer implements ServletContainerInitializer {
    private static final Logger log = LoggerFactory.getLogger(DefaultServletContainerInitializer.class);

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext context) throws ServletException {
        log.info("Registering default servlets");
        var properties = BeanRegistry.findBeanByTypeOrThrow(ApplicationProperties.Application.class);
        var jsonMapper = BeanRegistry.findBeanByTypeOrThrow(JsonMapper.class);

        var dispatcherServlet = context.addServlet("dispatcherServlet", new DispatcherServlet(jsonMapper));
        dispatcherServlet.addMapping(properties.webConfig().contextPath());

        if (properties.management().managementEndpointEnabled()) {
            var managementServlet = context.addServlet("managementServlet", new ManagementServlet(jsonMapper, properties));
            managementServlet.addMapping(properties.management().endpoint().path());
        }
    }
}
