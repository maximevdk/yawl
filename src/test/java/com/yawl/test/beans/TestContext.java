package com.yawl.test.beans;

import com.yawl.ApplicationPropertiesInitializer;
import com.yawl.TomcatWebServer;
import com.yawl.beans.ApplicationContext;
import com.yawl.beans.BeanService;
import com.yawl.beans.CommonBeans;
import com.yawl.configuration.CommonConfiguration;
import com.yawl.configuration.WebConfiguration;
import com.yawl.events.Event;
import com.yawl.events.EventListenerRegistrar;
import com.yawl.events.EventPublisher;

import java.util.Set;

public class TestContext {
    public ApplicationContext buildTestContext(Set<Class<?>> classes, String defaultConfigLocation) {
        var ctx = new ApplicationContext();
        var eventPublisher = new NoOpEventListenerRegistrar();

        var beanService = new BeanService(ctx, eventPublisher);
        beanService.loadAndInitializeConfig(CommonConfiguration.class);
        beanService.loadAndInitializeConfig(WebConfiguration.class);

        ctx.register(CommonBeans.APPLICATION_PROPERTIES_NAME, ctx.getBeanByTypeOrThrow(ApplicationPropertiesInitializer.class).init(defaultConfigLocation));
        ctx.register(CommonBeans.EVENT_PUBLISHER_NAME, eventPublisher, EventPublisher.class);

        beanService.loadAndInitializeBeans(classes);

        new TomcatWebServer().start(ctx);
        return ctx;
    }


    static class NoOpEventListenerRegistrar implements EventPublisher, EventListenerRegistrar {
        @Override
        public void registerListeners(Object bean) {
        }

        @Override
        public void publish(Event event) {
        }
    }

}
