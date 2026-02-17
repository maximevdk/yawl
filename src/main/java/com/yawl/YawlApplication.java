package com.yawl;

import com.yawl.beans.ApplicationContext;
import com.yawl.beans.CommonBeans;
import com.yawl.events.ApplicationEvent;
import com.yawl.events.EventPublisher;
import com.yawl.events.EventRegistry;
import com.yawl.util.ReflectionUtil;
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

        //initialize and register basic beans
        var yamlMapper = JacksonConfiguration.buildYamlMapper();
        var jsonMapper = JacksonConfiguration.buildJsonMapper();
        var properties = getMergedApplicationConfiguration(yamlMapper, args);

        //initialize event handler
        var registry = new EventRegistry();

        var ctx = new ApplicationContext();
        ctx.register(CommonBeans.APPLICATION_PROPERTIES_NAME, properties);
        ctx.register(CommonBeans.YAML_MAPPER_NAME, yamlMapper);
        ctx.register(CommonBeans.JSON_MAPPER_NAME, jsonMapper);
        ctx.register(CommonBeans.EVENT_PUBLISHER_NAME, registry, EventPublisher.class);

        //initialize user defined beans
        var beanCreationService = new BeanCreationService(ctx, registry);
        beanCreationService.findAndRegisterBeans();
        registry.publish(new ApplicationEvent.ApplicationContextInitialized(ctx));

        if (properties.web().enabled()) {
            if (properties.management().managementEndpointEnabled()) {
                var servlet = new ManagementServlet(properties, jsonMapper);
                ctx.register("managementServlet", servlet);
                registry.registerListeners(servlet);
            }

            var tomcat = new TomcatWebServer(ctx).start();
            registry.publish(new ApplicationEvent.ApplicationContextRefreshed(ctx));
            //start tomcat in a non daemon thread this results in the application not
            // blocking on this line but advancing to the next line, returning the context
            startNonDaemonAwaitThread(tomcat, baseClass);
        }

        return ctx;
    }

    private static ApplicationProperties.Application getMergedApplicationConfiguration(YAMLMapper yamlMapper, String... args) {
        var defaultConfigLocation = Stream.of(args)
                .filter(arg -> arg.startsWith("--config.location"))
                .map(arg -> arg.replace("--config.location=", ""))
                .findFirst().orElse("defaults.yml");

        log.debug("Using default configuration location: {}", defaultConfigLocation);

        var initializer = new ApplicationPropertiesInitializer(yamlMapper);
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
