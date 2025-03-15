package com.yawl;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.yawl.annotations.GetMapping;
import com.yawl.annotations.WebController;
import com.yawl.beans.BeanRegistry;
import com.yawl.exception.DuplicateRouteException;
import com.yawl.model.Destination;
import com.yawl.model.Header;
import com.yawl.model.HttpMethod;
import com.yawl.model.Route;
import com.yawl.util.ConstructorUtil;
import com.yawl.util.ReflectionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.yawl.util.StringUtil.decapitalize;

public class DispatcherServlet extends HttpServlet {
    private static Logger log = LoggerFactory.getLogger(DispatcherServlet.class);

    private final JsonMapper mapper;
    private final ReflectionUtil reflectionUtil;
    private Map<Route, Destination> routes;

    public DispatcherServlet(JsonMapper mapper, ReflectionUtil reflectionUtil) {
        this.mapper = mapper;
        this.reflectionUtil = reflectionUtil;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Handling request {} - {}", req.getMethod(), req.getRequestURI());
        findAndRegisterRoutes();

        var destination = routes.get(Route.of(HttpMethod.valueOf(req.getMethod()), req.getRequestURI()));

        if (destination == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Route %s not found".formatted(req.getRequestURI()));
            return;
        }

        var result = invokeMethod(destination);

        if (result.isPresent()) {
            resp.addHeader(Header.CONTENT_TYPE, destination.mediaType());
            mapper.writeValue(resp.getOutputStream(), result.get());
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Route %s not found".formatted(req.getRequestURI()));
        }
    }

    private Optional<Object> invokeMethod(Destination destination) {
        try {
            var controller = BeanRegistry.findBeanByType(destination.controller());
            var result = reflectionUtil.invokeMethodOnInstance(controller, destination.methodName());
            return Optional.of(result);
        } catch (Exception ex) {
            log.error("Unable to invoke method {} on {}.", destination.methodName(), destination, ex);
            return Optional.empty();
        }
    }


    private void findAndRegisterRoutes() {
        if (routes != null) {
            log.info("Dispatcher routes already initialized, returning");
            return;
        }
        // jit route building
        routes = new HashMap<>();

        var controllers = reflectionUtil.getClassesAnnotatedWith(WebController.class);
        log.info("Found controllers to analyze for paths {}", controllers);

        for (Class<?> controller : controllers) {
            log.info("Scanning controller {} for mapping annotations", controller.getName());

            //if we were unable to create a bean of the controller, we can skip looking for the methods
            var params = getConstructorParamsForController(controller);
            var instance = ConstructorUtil.newInstance(controller, params);
            instance.ifPresent(controllerInstance ->
                    BeanRegistry.registerBean(decapitalize(controller.getName()), controllerInstance));

            if (instance.isEmpty()) {
                continue;
            }

            var annotation = controller.getAnnotation(WebController.class);
            var basePath = annotation.path();

            for (Method method : controller.getMethods()) {
                if (method.isAnnotationPresent(GetMapping.class)) {
                    GetMapping getMapping = method.getAnnotation(GetMapping.class);

                    var methodPath = getMapping.path();

                    log.info("Found final path: {}/{}", basePath, methodPath);


                    var route = Route.of(HttpMethod.GET, basePath, methodPath);
                    if (routes.containsKey(route)) {
                        throw DuplicateRouteException.forRoute(route);
                    }

                    routes.put(route, new Destination(controller, method.getName(), getMapping.produces()));
                }
            }
        }
    }

    private Object[] getConstructorParamsForController(Class<?> controller) {
        return ConstructorUtil.getRequiredConstructorParameters(controller).stream()
                .map(BeanRegistry::findBeanByType)
                .toArray();
    }
}
