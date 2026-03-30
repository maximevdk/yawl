package com.yawl.configuration;

import com.yawl.configuration.model.PropertySource;

import java.util.List;
import java.util.Objects;

public record Environment(List<PropertySource> sources) {

    public String getProperty(String key, String defaultValue) {
        return sources.stream()
                .map(source -> source.getProperty(key))
                .filter(Objects::nonNull)
                .findFirst().orElse(defaultValue);
    }
}
