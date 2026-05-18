package com.yawl.configuration.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class YamlConfigurationFilePropertySourceTest {
    @Test
    void getProperty() {
        var properties = YamlConfigurationFilePropertySource.init(getClass().getClassLoader().getResourceAsStream("defaults.yml"));

        assertEquals("yawl application", properties.getProperty("application.name"));
        assertEquals("application.yml", properties.getProperty("application.config.location"));
        assertEquals("true", properties.getProperty("application.web.enabled"));
        assertEquals("8080", properties.getProperty("application.web.config.port"));
        assertEquals("/", properties.getProperty("application.web.config.context-path"));
        assertEquals("true", properties.getProperty("application.web.config.virtual-threads.enabled"));
        assertEquals("virtual-thread-", properties.getProperty("application.web.config.virtual-threads.name"));
        assertEquals("false", properties.getProperty("application.management.endpoint.enabled"));
        assertEquals("/health", properties.getProperty("application.management.endpoint.path"));
    }
}