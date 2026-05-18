package com.yawl.configuration;

import com.yawl.common.util.StringUtils;
import com.yawl.configuration.model.PropertySource;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        return getProperty(key).orElse(defaultValue);
    }

    public <T> T getProperty(String key, T defaultValue) {
        return (T) getProperty(key)
                .map(value -> StringUtils.parse(value, defaultValue.getClass()))
                .orElse(defaultValue);
    }

    private Optional<String> getProperty(String key) {
        return sources.stream()
                .map(source -> source.getProperty(key))
                .filter(Objects::nonNull)
                .findFirst();
    }


}
