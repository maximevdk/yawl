package com.yawl;

import com.yawl.configuration.model.PropertySource;

import java.util.Map;

public class MapPropertySource implements PropertySource {
    private final Map<String, String> properties;

    public MapPropertySource(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public String getProperty(String key) {
        return properties.get(key);
    }

}
