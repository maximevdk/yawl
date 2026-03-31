package com.yawl.http.model;

import java.lang.reflect.Method;

/**
 * A route that has been registered with a handler method and response metadata.
 *
 * @param route        the route definition
 * @param method       the handler method to invoke
 * @param responseInfo the response content type and status
 */
public record RegisteredRoute(Route route, Method method, ResponseInfo responseInfo) {
}
