package com.yawl.configuration;

import com.yawl.configuration.model.PropertySource;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ConfigurableEnvironment {
    private final List<PropertySource> sources = new ArrayList<>();

    public ConfigurableEnvironment addPropertySource(PropertySource propertySource) {
        sources.add(propertySource);
        return this;
    }

    public Optional<String> getProperty(String key) {
        return sources.stream()
                .map(propertySource -> propertySource.getProperty(key))
                .filter(Objects::nonNull)
                .findFirst();
    }

    public Environment build() {
        return new Environment(sources);
    }

    public static ConfigurableEnvironment builder() {
        return new ConfigurableEnvironment();
    }
}
