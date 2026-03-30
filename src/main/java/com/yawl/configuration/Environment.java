package com.yawl.configuration;

import com.yawl.configuration.model.PropertySource;

import java.util.List;

public record Environment(List<PropertySource> sources) {

    public String getProperty(String key, String defaultValue) {
        return sources.stream()
                .map(source -> source.getProperty(key))
                .findFirst().orElse(defaultValue);
    }
}
