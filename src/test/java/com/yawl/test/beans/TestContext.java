package com.yawl.test.beans;

import com.yawl.ApplicationPropertiesInitializer;
import com.yawl.BeanCreationService;
import com.yawl.TomcatWebServer;
import com.yawl.beans.ApplicationContext;
import com.yawl.beans.CommonBeans;
import com.yawl.configuration.CommonConfiguration;
import com.yawl.configuration.WebConfiguration;
import com.yawl.events.Event;
import com.yawl.events.EventListenerRegistrar;
import com.yawl.events.EventPublisher;
import com.yawl.common.util.ReflectionUtil;
import tools.jackson.dataformat.yaml.YAMLMapper;

import java.util.Set;

public class TestContext {

    public TestContext(Class<?> testClass) {
        ReflectionUtil.init(testClass);
    }

    public ApplicationContext buildTestContext(Set<Class<?>> classes, String defaultConfigLocation) {
        var ctx = new ApplicationContext();
        var eventPublisher = new NoOpEventListenerRegistrar();
        var beanCreationService = new BeanCreationService(ctx, eventPublisher);
        beanCreationService.findAndRegisterBeans(CommonConfiguration.class, WebConfiguration.class);
        var properties = new ApplicationPropertiesInitializer(ctx.getBeanByTypeOrThrow(YAMLMapper.class));

        ctx.register(CommonBeans.APPLICATION_PROPERTIES_NAME, properties.init(defaultConfigLocation));
        ctx.register(CommonBeans.EVENT_PUBLISHER_NAME, eventPublisher, EventPublisher.class);

        beanCreationService.findAndRegisterBeans(classes);

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
