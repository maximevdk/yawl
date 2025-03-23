package com.yawl.model;

import java.util.Map;

public record Debug(Map<String, Class<?>> beans, Map<String, Class<?>> routes) {
}
