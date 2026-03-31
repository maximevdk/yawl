package com.yawl.configuration.model;

/**
 * Abstraction for a source of configuration properties.
 */
public interface PropertySource {
    /**
     * Returns the property value associated with the given key or {@code null} if not present.
     *
     * @param key the property key
     * @return the property value, or {@code null}
     */
    String getProperty(String key);
}
