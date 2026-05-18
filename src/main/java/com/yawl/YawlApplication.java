package com.yawl;

import com.yawl.beans.ApplicationContext;
import com.yawl.beans.BeanService;
import com.yawl.beans.model.CommonBeans;
import com.yawl.configuration.CommonConfiguration;
import com.yawl.configuration.ConfigurableEnvironment;
import com.yawl.configuration.Environment;
import com.yawl.configuration.model.CommandLinePropertySource;
import com.yawl.events.ApplicationEvent;
import com.yawl.events.EventRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class YawlApplication {
    private static final Logger log = LoggerFactory.getLogger(YawlApplication.class);

    public static ApplicationContext run(Class<?> baseClass, String... args) {
        // create application context
        var ctx = new ApplicationContext();
        var configurableEnvironment = ConfigurableEnvironment.builder()
                .addPropertySource(CommandLinePropertySource.from(args));
        new ConfigDataLocationResolver().applyTo(configurableEnvironment);

        ctx.register(CommonBeans.ENVIRONMENT_NAME, configurableEnvironment.build(), Environment.class);

        //initialize event handler
        var registry = new EventRegistry();
        ctx.register(CommonBeans.EVENT_PUBLISHER_NAME, registry);

        var beanService = new BeanService(ctx);
        //initialize and register basic beans
        beanService.loadAndInitializeConfig(CommonConfiguration.class);

        //initialize user defined beans
        var basePackage = Optional.ofNullable(baseClass).map(Class::getPackage).orElseThrow(() -> new RuntimeException("Can't run application from nameless package"));
        beanService.loadAndInitializeBeans(basePackage);
        registry.publish(new ApplicationEvent.ApplicationContextInitialized(ctx));

        if (ctx.containsBeanName(CommonBeans.WEB_SERVER_NAME)) {
            var webserver = ctx.getBeanByNameOrThrow(CommonBeans.WEB_SERVER_NAME, WebServer.class);
            webserver.start(ctx);
        }

        return ctx;
    }
}
