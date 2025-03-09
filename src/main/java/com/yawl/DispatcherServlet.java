package com.yawl;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.yawl.annotations.GetMapping;
import com.yawl.annotations.WebController;
import com.yawl.beans.BeanLoader;
import com.yawl.exception.DuplicateRouteException;
import com.yawl.exception.NoAccessibleConstructorFoundException;
import com.yawl.model.Destination;
import com.yawl.model.HttpMethod;
import com.yawl.model.Route;
import com.yawl.util.ReflectionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DispatcherServlet extends HttpServlet {
    private static Logger log = LoggerFactory.getLogger(DispatcherServlet.class);

    private final JsonMapper mapper;
    private Map<Route, Destination> routes;

    public DispatcherServlet() {
        this.mapper = BeanLoader.getBeanByName("jsonMapper", JsonMapper.class);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("Handling request {} - {}", req.getMethod(), req.getRequestURI());
        findAndRegisterRoutes();

        var destination = routes.get(Route.of(HttpMethod.valueOf(req.getMethod()), req.getRequestURI()));

        if (destination == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Route %s not found".formatted(req.getRequestURI()));
        }

        var result = invokeMethod(destination);

        if (result.isPresent()) {
            mapper.writeValue(resp.getOutputStream(), result.get());
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Route %s not found".formatted(req.getRequestURI()));
        }
    }

    private Optional<Object> invokeMethod(Destination destination) {
        try {
            var controller = BeanLoader.getBeanByName(destination.controllerBeanName());
            var result = controller.getClass().getMethod(destination.methodName()).invoke(controller);
            return Optional.of(result);
        } catch (Exception ex) {
            log.error("Unable to invoke method {} on {}.", destination.methodName(), destination.controllerBeanName(), ex);
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

        var reflectionUtil = ReflectionUtil.instance(BeanLoader.getBeanByName("basePackageName", String.class));
        var controllers = reflectionUtil.getClassesAnnotatedWith(WebController.class);
        log.info("Found controllers to analyze for paths {}", controllers);

        for (Class<?> controller : controllers) {
            log.info("Scanning controller {} for mapping annotations", controller.getName());

            //if we were unable to create a bean of the controller, we can skip looking for the methods
            if (!createControllerBean(controller)) {
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

                    routes.put(route, new Destination(controller.getName(), method.getName()));
                }
            }
        }
    }

    private boolean createControllerBean(Class<?> controller) {
        log.info("Finding suitable constructor for controller {}", controller.getDeclaredConstructors());
        var constructor = Arrays.stream(controller.getDeclaredConstructors())
                .filter(c -> c.canAccess(null)) // todo: find a better way to figure out if accessible
                .findFirst().orElseThrow(() -> NoAccessibleConstructorFoundException.forClass(controller));

        try {
            Object instance = constructor.newInstance();
            BeanLoader.createBean(controller.getName(), instance);
            return true;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            //ignore because it is our fault :grim:
            return false;
        }
    }
}
