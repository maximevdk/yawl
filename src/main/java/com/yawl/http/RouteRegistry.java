package com.yawl.http;

import com.yawl.annotations.GetMapping;
import com.yawl.annotations.PostMapping;
import com.yawl.annotations.WebController;
import com.yawl.beans.ApplicationContext;
import com.yawl.exception.DuplicateRouteException;
import com.yawl.http.model.ContentType;
import com.yawl.http.model.HttpMethod;
import com.yawl.http.model.RegisteredRoute;
import com.yawl.http.model.ResponseInfo;
import com.yawl.http.model.Route;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Registry of the available routes in the application
 */
public class RouteRegistry {
    private static final Logger log = LoggerFactory.getLogger(RouteRegistry.class);
    private final Map<Route, RegisteredRoute> destinations = new HashMap<>();

    public void init(ApplicationContext applicationContext) {
        var controllers = applicationContext.getBeansAnnotatedWith(WebController.class);

        for (Class<?> controller : controllers) {
            log.info("Analyzing controller for routes {}", controller);
            var webController = controller.getAnnotation(WebController.class);

            for (Method method : controller.getDeclaredMethods()) {
                if (method.isAnnotationPresent(GetMapping.class)) {
                    var getMapping = method.getAnnotation(GetMapping.class);
                    var route = Route.get(webController.path(), getMapping.path());
                    var responseInfo = new ResponseInfo(ContentType.of(getMapping.produces()), getMapping.status());
                    registerRoute(route, method, responseInfo);
                } else if (method.isAnnotationPresent(PostMapping.class)) {
                    var postMapping = method.getAnnotation(PostMapping.class);
                    var route = Route.post(webController.path(), postMapping.path());
                    var responseInfo = new ResponseInfo(ContentType.of(postMapping.produces()), postMapping.status());
                    registerRoute(route, method, responseInfo);
                }
            }
        }
    }

    public Optional<RegisteredRoute> find(String method, String path) {
        var httpMethod = HttpMethod.valueOf(method);

        //contains key can only be used when defining routes but never when finding a match
        //because of matching a path to a route with segments and potential capture parameters
        return destinations.entrySet()
                .stream()
                .filter(entry -> entry.getKey().matches(httpMethod, path))
                .findFirst()
                .map(Map.Entry::getValue);
    }

    public List<RegisteredRoute> getRoutes() {
        return List.copyOf(destinations.values());
    }

    private void registerRoute(Route route, Method method, ResponseInfo info) {
        //only place we can use containsKey because we're defining new routes where we know when
        // a path segment is a capture group or not
        if (destinations.containsKey(route)) {
            throw DuplicateRouteException.forRoute(route);
        }

        destinations.put(route, new RegisteredRoute(route, method, info));
    }
}
