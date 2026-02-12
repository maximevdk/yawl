package com.yawl.http.model;

import java.lang.reflect.Method;

public record RegisteredRoute(Route route, Method method, ResponseInfo responseInfo) {
}
