package com.yawl;

import com.yawl.annotations.WebFilter;
import com.yawl.common.util.ApplicationContextUtils;
import com.yawl.common.util.BeanUtil;
import com.yawl.configuration.ApplicationProperties;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;
import java.util.Set;

public class DefaultServletContainerInitializer implements ServletContainerInitializer {
    private static final Logger log = LoggerFactory.getLogger(DefaultServletContainerInitializer.class);

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext context) throws ServletException {
        context.addListener(new TomcatWebAppInitializer());

        log.debug("Registering default servlets");
        var applicationContext = ApplicationContextUtils.getApplicationContext(context);
        var properties = applicationContext.getBeanByTypeOrThrow(ApplicationProperties.Application.class);

        if (applicationContext.containsBeanOfType(ManagementServlet.class)) {
            var managementServlet = context.addServlet("managementServlet", applicationContext.getBeanByTypeOrThrow(ManagementServlet.class));
            managementServlet.addMapping(properties.management().endpoint().path());
        }

        var dispatcherServlet = context.addServlet("dispatcherServlet", new DispatcherServlet());
        dispatcherServlet.addMapping(properties.web().config().contextPath());
        dispatcherServlet.setAsyncSupported(true);
        dispatcherServlet.setLoadOnStartup(1);

        // registering user filters
        applicationContext.getBeansAnnotatedWith(WebFilter.class)
                .stream()
                .filter(Filter.class::isAssignableFrom)
                .forEach(filter -> {
                    log.debug("Registering filer {}", filter.getName());
                    var webfilter = filter.getAnnotation(WebFilter.class);
                    context.addFilter(BeanUtil.getBeanName(filter), (Filter) applicationContext.getBeanByTypeOrThrow(filter))
                            .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, webfilter.urlPatterns());
                });
    }
}
