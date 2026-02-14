package com.yawl;

import com.yawl.beans.ApplicationContext;
import com.yawl.beans.CommonBeans;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class DefaultServletContainerInitializer implements ServletContainerInitializer {
    private static final Logger log = LoggerFactory.getLogger(DefaultServletContainerInitializer.class);

    private final ApplicationContext applicationContext;

    public DefaultServletContainerInitializer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext context) throws ServletException {
        log.debug("Registering default servlets");
        var properties = applicationContext.getBeanByNameOrThrow(CommonBeans.APPLICATION_PROPERTIES_NAME, ApplicationProperties.Application.class);

        if (applicationContext.containsBeanOfType(ManagementServlet.class)) {
            var managementServlet = context.addServlet("managementServlet", applicationContext.getBeanByTypeOrThrow(ManagementServlet.class));
            managementServlet.addMapping(properties.management().endpoint().path());
        }

        var dispatcherServlet = context.addServlet("dispatcherServlet", new DispatcherServlet(applicationContext));
        dispatcherServlet.addMapping(properties.web().config().contextPath());
        dispatcherServlet.setAsyncSupported(true);
    }
}
