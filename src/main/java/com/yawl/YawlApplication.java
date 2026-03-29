package com.yawl;

import com.yawl.beans.ApplicationContext;
import com.yawl.beans.BeanService;
import com.yawl.beans.CommonBeans;
import com.yawl.configuration.ApplicationProperties;
import com.yawl.configuration.CommonConfiguration;
import com.yawl.configuration.WebConfiguration;
import com.yawl.events.ApplicationEvent;
import com.yawl.events.EventPublisher;
import com.yawl.events.EventRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

public class YawlApplication {
    private static final Logger log = LoggerFactory.getLogger(YawlApplication.class);

    public static ApplicationContext run(Class<?> baseClass, String... args) {
        // create application context
        var ctx = new ApplicationContext();
        //initialize event handler
        var registry = new EventRegistry();
        ctx.register(CommonBeans.EVENT_PUBLISHER_NAME, registry, EventPublisher.class);

        var beanService = new BeanService(ctx, registry);
        //initialize and register basic beans
        beanService.loadAndInitializeConfig(CommonConfiguration.class);

        // build system configuration properties
        var properties = getMergedApplicationConfiguration(ctx, args);
        ctx.register(CommonBeans.APPLICATION_PROPERTIES_NAME, properties);

        //initialize user defined beans
        beanService.loadAndInitializeBeans(baseClass);
        registry.publish(new ApplicationEvent.ApplicationContextInitialized(ctx));

        if (properties.web().enabled()) {
            beanService.loadAndInitializeConfig(WebConfiguration.class);
            registry.publish(new ApplicationEvent.ApplicationContextRefreshed(ctx));

            if (properties.management().managementEndpointEnabled()) {
                var servlet = new ManagementServlet();
                ctx.register("managementServlet", servlet, ManagementServlet.class);
                registry.registerListeners(servlet);
            }

            var webserver = new TomcatWebServer();
            webserver.start(ctx);

            ctx.register(CommonBeans.WEB_SERVER_NAME, webserver);
            registry.publish(new ApplicationEvent.ApplicationContextRefreshed(ctx));
        }

        return ctx;
    }

    private static ApplicationProperties.Application getMergedApplicationConfiguration(ApplicationContext ctx, String... args) {
        var defaultConfigLocation = Stream.of(args)
                .filter(arg -> arg.startsWith("--config.location="))
                .map(arg -> arg.replace("--config.location=", ""))
                .findFirst().orElse("defaults.yml");

        log.debug("Using default configuration location: {}", defaultConfigLocation);
        return ctx.getBeanByTypeOrThrow(ApplicationPropertiesInitializer.class).init(defaultConfigLocation);
    }
}
