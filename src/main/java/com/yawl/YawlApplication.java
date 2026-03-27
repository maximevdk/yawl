package com.yawl;

import com.yawl.beans.ApplicationContext;
import com.yawl.beans.CommonBeans;
import com.yawl.common.util.ReflectionUtil;
import com.yawl.configuration.ApplicationProperties;
import com.yawl.configuration.CommonConfiguration;
import com.yawl.configuration.WebConfiguration;
import com.yawl.events.ApplicationEvent;
import com.yawl.events.EventPublisher;
import com.yawl.events.EventRegistry;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.dataformat.yaml.YAMLMapper;

import java.util.stream.Stream;

public class YawlApplication {
    private static final Logger log = LoggerFactory.getLogger(YawlApplication.class);

    public static ApplicationContext run(Class<?> baseClass, String... args) {
        //initialize reflection
        ReflectionUtil.init(baseClass);

        // create application context
        var ctx = new ApplicationContext();
        //initialize event handler
        var registry = new EventRegistry();
        var beanCreationService = new BeanCreationService(ctx, registry);

        //initialize and register basic beans
        beanCreationService.findAndRegisterBeans(CommonConfiguration.class);
        var properties = getMergedApplicationConfiguration(ctx, args);

        ctx.register(CommonBeans.APPLICATION_PROPERTIES_NAME, properties);
        ctx.register(CommonBeans.EVENT_PUBLISHER_NAME, registry, EventPublisher.class);

        //initialize user defined beans
        beanCreationService.findAndRegisterBeans();
        registry.publish(new ApplicationEvent.ApplicationContextInitialized(ctx));

        if (properties.web().enabled()) {
            beanCreationService.findAndRegisterBeans(WebConfiguration.class);

            if (properties.management().managementEndpointEnabled()) {
                var servlet = new ManagementServlet();
                ctx.register("managementServlet", servlet, ManagementServlet.class);
                registry.registerListeners(servlet);
            }

            var webserver = new TomcatWebServer();
            webserver.start(ctx);

            ctx.register(CommonBeans.WEB_SERVER_NAME, webserver);
            registry.publish(new ApplicationEvent.ApplicationContextRefreshed(ctx));
            //start tomcat in a non daemon thread this results in the application not
            // blocking on this line but advancing to the next line, returning the context
            startNonDaemonAwaitThread(webserver.getTomcat(), baseClass);
        }

        return ctx;
    }

    private static ApplicationProperties.Application getMergedApplicationConfiguration(ApplicationContext ctx, String... args) {
        var defaultConfigLocation = Stream.of(args)
                .filter(arg -> arg.startsWith("--config.location"))
                .map(arg -> arg.replace("--config.location=", ""))
                .findFirst().orElse("defaults.yml");

        log.debug("Using default configuration location: {}", defaultConfigLocation);

        var initializer = new ApplicationPropertiesInitializer(ctx.getBeanByTypeOrThrow(YAMLMapper.class));
        return initializer.init(defaultConfigLocation);
    }

    private static void startNonDaemonAwaitThread(Tomcat tomcat, Class<?> baseClass) {
        var awaitThread = new Thread(() -> {
            tomcat.getServer().await();
        });
        awaitThread.setContextClassLoader(baseClass.getClassLoader());
        awaitThread.setDaemon(false);  // non-daemon keeps JVM alive
        awaitThread.start();
    }
}
