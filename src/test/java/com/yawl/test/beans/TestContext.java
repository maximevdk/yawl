package com.yawl.test.beans;

import com.yawl.ApplicationPropertiesInitializer;
import com.yawl.BeanCreationService;
import com.yawl.JacksonConfiguration;
import com.yawl.TomcatWebServer;
import com.yawl.beans.ApplicationContext;
import com.yawl.beans.CommonBeans;
import com.yawl.events.Event;
import com.yawl.events.EventListenerRegistrar;
import com.yawl.events.EventPublisher;

import java.util.Set;

public class TestContext {

    public ApplicationContext buildTestContext(Set<Class<?>> classes, String defaultConfigLocation) {
        var yamlMapper = JacksonConfiguration.buildYamlMapper();
        var jsonMapper = JacksonConfiguration.buildJsonMapper();
        var properties = new ApplicationPropertiesInitializer(yamlMapper).init(defaultConfigLocation);
        var eventPublisher = new NoOpEventListenerRegistrar();

        var ctx = new ApplicationContext();
        ctx.register(CommonBeans.YAML_MAPPER_NAME, yamlMapper);
        ctx.register(CommonBeans.JSON_MAPPER_NAME, jsonMapper);
        ctx.register(CommonBeans.APPLICATION_PROPERTIES_NAME, properties);
        ctx.register(CommonBeans.EVENT_PUBLISHER_NAME, eventPublisher, EventPublisher.class);

        var beanCreationService = new BeanCreationService(ctx, eventPublisher);
        beanCreationService.findAndRegisterBeans(classes);

        new TomcatWebServer(ctx).start();
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
