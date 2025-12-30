package com.yawl.model;

import java.util.Map;
import java.util.Set;

public record Debug(Map<String, Class<?>> beans, Set<Route> routes) {
}
