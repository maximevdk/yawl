package com.yawl.model;

import java.util.Arrays;

/**
 * Enumeration of available management endpoint types.
 */
public enum ManagementEndpointType {
    /** Debug endpoint providing internal bean and route information. */
    DEBUG("debug"),
    /** Health endpoint providing runtime health metrics. */
    HEALTH("health");

    private final String name;

    ManagementEndpointType(String name) {
        this.name = name;
    }

    /**
     * Returns the endpoint name used in configuration.
     *
     * @return the endpoint name
     */
    public String endpointName() {
        return this.name;
    }

    /**
     * Returns the management endpoint type matching the given name (case-insensitive).
     *
     * @param name the endpoint name
     * @return the matching endpoint type
     * @throws IllegalArgumentException if no match is found
     */
    public static ManagementEndpointType of(String name) {
        return Arrays.stream(values())
                .filter(endpoint -> endpoint.endpointName().equalsIgnoreCase(name))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
