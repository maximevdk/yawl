package com.yawl;

import com.yawl.beans.ApplicationContext;
import com.yawl.beans.BeanService;
import com.yawl.beans.model.CommonBeans;
import com.yawl.configuration.ApplicationProperties;
import com.yawl.configuration.CommonConfiguration;
import com.yawl.configuration.Environment;
import com.yawl.configuration.WebConfiguration;
import com.yawl.configuration.model.CommandLinePropertySource;
import com.yawl.events.ApplicationEvent;
import com.yawl.events.EventRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class YawlApplication {
    private static final Logger log = LoggerFactory.getLogger(YawlApplication.class);

    public static ApplicationContext run(Class<?> baseClass, String... args) {
        // create application context
        var ctx = new ApplicationContext();
        var environment = new Environment(List.of(CommandLinePropertySource.from(args)));
        ctx.register(CommonBeans.ENVIRONMENT_NAME, environment, Environment.class);

        //initialize event handler
        var registry = new EventRegistry();
        ctx.register(CommonBeans.EVENT_PUBLISHER_NAME, registry);

        var beanService = new BeanService(ctx);
        //initialize and register basic beans
        beanService.loadAndInitializeConfig(CommonConfiguration.class);

        //initialize user defined beans
        beanService.loadAndInitializeBeans(baseClass);
        registry.publish(new ApplicationEvent.ApplicationContextInitialized(ctx));

        var properties = ctx.getBeanByTypeOrThrow(ApplicationProperties.Application.class);
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
}
