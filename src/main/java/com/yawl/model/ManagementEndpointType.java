package com.yawl.model;

import java.util.Arrays;

public enum ManagementEndpointType {
    DEBUG("debug"),
    HEALTH("health");

    private final String name;

    ManagementEndpointType(String name) {
        this.name = name;
    }

    public String endpointName() {
        return this.name;
    }

    public static ManagementEndpointType of(String name) {
        return Arrays.stream(values())
                .filter(endpoint -> endpoint.endpointName().equalsIgnoreCase(name))
                .findFirst().orElseThrow(IllegalArgumentException::new);
    }
}
