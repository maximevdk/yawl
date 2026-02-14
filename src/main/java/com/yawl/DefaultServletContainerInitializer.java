package com.yawl;

import com.yawl.beans.ApplicationContext;
import com.yawl.beans.CommonBeans;
import com.yawl.events.EventListenerRegistrar;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

import java.util.Set;

public class DefaultServletContainerInitializer implements ServletContainerInitializer {
    private static final Logger log = LoggerFactory.getLogger(DefaultServletContainerInitializer.class);

    private final ApplicationContext applicationContext;

    public DefaultServletContainerInitializer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext context) throws ServletException {
        log.info("Registering default servlets");
        var properties = applicationContext.getBeanByNameOrThrow(CommonBeans.APPLICATION_PROPERTIES_NAME, ApplicationProperties.Application.class);

        if (properties.management().managementEndpointEnabled()) {
            var jsonMapper = applicationContext.getBeanByNameOrThrow(CommonBeans.JSON_MAPPER_NAME, JsonMapper.class);
            var servlet = new ManagementServlet(properties, jsonMapper);
            applicationContext.getBeanByNameOrThrow(CommonBeans.EVENT_REGISTRY_NAME, EventListenerRegistrar.class)
                    .registerBean(servlet);

            var managementServlet = context.addServlet("managementServlet", servlet);
            managementServlet.addMapping(properties.management().endpoint().path());
        }

        var dispatcherServlet = context.addServlet("dispatcherServlet", new DispatcherServlet(applicationContext));
        dispatcherServlet.addMapping(properties.web().config().contextPath());
        dispatcherServlet.setAsyncSupported(true);
    }
}
