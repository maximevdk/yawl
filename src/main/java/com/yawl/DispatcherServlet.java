package com.yawl;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.yawl.annotations.GetMapping;
import com.yawl.annotations.PostMapping;
import com.yawl.annotations.QueryParam;
import com.yawl.annotations.WebController;
import com.yawl.beans.BeanRegistry;
import com.yawl.exception.DuplicateRouteException;
import com.yawl.exception.NoSuchMethodException;
import com.yawl.exception.RequiredRequestParameterMissingException;
import com.yawl.model.*;
import com.yawl.util.ConstructorUtil;
import com.yawl.util.ReflectionUtil;
import com.yawl.util.StringUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;

import static com.yawl.util.StringUtils.decapitalize;

public class DispatcherServlet extends HttpServlet {
    private static Logger log = LoggerFactory.getLogger(DispatcherServlet.class);

    private final JsonMapper mapper;
    private Map<Route, RequestDestination> routes;

    public DispatcherServlet(JsonMapper mapper) {
        this.mapper = mapper;
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

        try {
            var result = invokeMethod(destination, req);
            if (result.isPresent()) {
                resp.addHeader(Header.CONTENT_TYPE, destination.method().produces().value());
                mapper.writeValue(resp.getOutputStream(), result.get());
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Route %s not found".formatted(req.getRequestURI()));
            }
        } catch (RequiredRequestParameterMissingException ex) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
    }

    private Optional<Object> invokeMethod(RequestDestination destination, HttpServletRequest request) {
        try {
            var controller = BeanRegistry.findBeanByTypeOrThrow(destination.controller());
            var result = ReflectionUtil.invokeMethodOnInstance(controller, destination.method().name(), getQueryParameters(destination.method().parameters(), request));
            return Optional.of(result);
        } catch (NoSuchMethodException ex) {
            log.error("Unable to invoke method {} on {}.", destination.method().name(), destination, ex);
            return Optional.empty();
        }
    }


    private void findAndRegisterRoutes() {
        if (routes != null) {
            log.debug("Dispatcher routes already initialized, returning");
            return;
        }
        // jit route building
        routes = new HashMap<>();

        var controllers = ReflectionUtil.getClassesAnnotatedWith(WebController.class);
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

                    log.info("Found final path: GET {}/{}", basePath, methodPath);

                    var route = Route.of(HttpMethod.GET, basePath, methodPath);
                    if (routes.containsKey(route)) {
                        throw DuplicateRouteException.forRoute(route);
                    }

                    routes.put(route, new RequestDestination(controller, new RequestMethod(method.getName(), getQueryParameters(method), MediaType.of(getMapping.produces()))));
                } else if (method.isAnnotationPresent(PostMapping.class)) {
                    PostMapping postMapping = method.getAnnotation(PostMapping.class);

                    var methodPath = postMapping.path();

                    log.info("Found final path: POST {}/{}", basePath, methodPath);

                    var route = Route.of(HttpMethod.POST, basePath, methodPath);
                    if (routes.containsKey(route)) {
                        throw DuplicateRouteException.forRoute(route);
                    }

                    routes.put(route, new RequestDestination(controller, new RequestMethod(method.getName(), getQueryParameters(method), MediaType.of(postMapping.produces()))));
                }
            }
        }
    }

    private Object[] getConstructorParamsForController(Class<?> controller) {
        return ConstructorUtil.getRequiredConstructorParameters(controller).stream()
                .map(BeanRegistry::findBeanByTypeOrThrow)
                .toArray();
    }

    private List<RequestParameter> getQueryParameters(Method method) {
        return Arrays.stream(method.getParameters())
                .filter(parameter -> parameter.isAnnotationPresent(QueryParam.class))
                .map(parameter -> {
                    var queryParam = parameter.getAnnotation(QueryParam.class);
                    var queryParamName = StringUtils.hasText(queryParam.name()) ? queryParam.name() : parameter.getName();

                    return new RequestParameter(queryParamName, parameter.getType(), queryParam.required());
                })
                .toList();
    }

    private Object[] getQueryParameters(List<RequestParameter> parameters, HttpServletRequest request) {
        return parameters.stream().map(parameter -> {
            var requestParameter = request.getParameter(parameter.name());

            if (requestParameter == null && parameter.required()) {
                throw RequiredRequestParameterMissingException.forParameter(parameter.name());
            }

            return StringUtils.parse(requestParameter, parameter.type());
        }).toArray();
    }
}
