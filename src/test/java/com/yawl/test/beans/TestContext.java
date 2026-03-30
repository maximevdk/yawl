package com.yawl.test.beans;

import com.yawl.TomcatWebServer;
import com.yawl.beans.ApplicationContext;
import com.yawl.beans.BeanService;
import com.yawl.beans.model.CommonBeans;
import com.yawl.configuration.CommonConfiguration;
import com.yawl.configuration.Environment;
import com.yawl.configuration.WebConfiguration;
import com.yawl.configuration.model.CommonProperties;
import com.yawl.configuration.model.PropertySource;
import com.yawl.events.Event;
import com.yawl.events.EventListenerRegistrar;
import com.yawl.events.EventPublisher;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestContext {
    public ApplicationContext buildTestContext(Set<Class<?>> classes, String defaultConfigLocation) {
        var ctx = new ApplicationContext();
        var environment = new Environment(List.of(new MapPropertySource(Map.of(CommonProperties.OVERWRITE_DEFAULTS_CONFIG_LOCATION, defaultConfigLocation))));
        ctx.register(CommonBeans.ENVIRONMENT_NAME, environment, Environment.class);

        var eventPublisher = new NoOpEventListenerRegistrar();
        ctx.register(CommonBeans.EVENT_PUBLISHER_NAME, eventPublisher);

        var beanService = new BeanService(ctx);
        beanService.loadAndInitializeConfig(CommonConfiguration.class);
        beanService.loadAndInitializeConfig(WebConfiguration.class);
        beanService.loadAndInitializeBeans(classes);

        new TomcatWebServer().start(ctx);
        return ctx;
    }

    static class MapPropertySource implements PropertySource {
        private final Map<String, String> properties;

        public MapPropertySource(Map<String, String> properties) {
            this.properties = properties;
        }

        @Override
        public String getProperty(String key) {
            return properties.get(key);
        }
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
