package com.yawl;

import com.yawl.annotations.WebFilter;
import com.yawl.configuration.ApplicationProperties;
import com.yawl.common.util.ApplicationContextUtils;
import com.yawl.common.util.BeanUtil;
import com.yawl.common.util.ConstructorUtil;
import com.yawl.common.util.ReflectionUtil;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.Filter;
import jakarta.servlet.ServletContainerInitializer;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Set;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

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
        ReflectionUtil.getClassesAnnotatedWith(WebFilter.class).stream()
                .filter(Filter.class::isAssignableFrom)
                .forEach(filter -> {
                    log.debug("Registering filer {}", filter.getName());
                    var webfilter = filter.getAnnotation(WebFilter.class);
                    var constructor = ConstructorUtil.getConstructorOrThrow(filter);

                    Arrays.stream(constructor.getParameters())
                            .map(parameter -> applicationContext.getBeanByTypeOrThrow(parameter.getType()))
                            .collect(collectingAndThen(toList(), dependencies -> ConstructorUtil.newInstance(constructor, dependencies)))
                            .ifPresent(instance -> context.addFilter(BeanUtil.getBeanName(filter), (Filter) instance)
                                    .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, webfilter.urlPatterns()));
                });
    }
}
