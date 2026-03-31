package com.yawl.configuration;

import com.yawl.configuration.model.PropertySource;

import java.util.List;
import java.util.Objects;

/**
 * Aggregates multiple {@link PropertySource} instances and resolves property values by checking each source in order.
 *
 * @param sources the ordered list of property sources
 */
public record Environment(List<PropertySource> sources) {

    /**
     * Returns the value of the given property key from the first source that contains it,
     * or the default value if no source provides it.
     *
     * @param key          the property key
     * @param defaultValue the fallback value
     * @return the resolved property value
     */
    public String getProperty(String key, String defaultValue) {
        return sources.stream()
                .map(source -> source.getProperty(key))
                .filter(Objects::nonNull)
                .findFirst().orElse(defaultValue);
    }
}
