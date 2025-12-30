package com.yawl;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.yawl.annotations.GetMapping;
import com.yawl.annotations.PathParam;
import com.yawl.annotations.PostMapping;
import com.yawl.annotations.QueryParam;
import com.yawl.annotations.WebController;
import com.yawl.beans.BeanRegistry;
import com.yawl.beans.CommonBeans;
import com.yawl.exception.DuplicateRouteException;
import com.yawl.exception.MissingPathParameterException;
import com.yawl.exception.RequiredRequestParameterMissingException;
import com.yawl.model.Header;
import com.yawl.model.HttpMethod;
import com.yawl.model.InvocationResult;
import com.yawl.model.MediaType;
import com.yawl.model.RequestDestination;
import com.yawl.model.RequestMethod;
import com.yawl.model.RequestParam;
import com.yawl.model.Route;
import com.yawl.model.ServerError;
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
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import static java.util.function.Predicate.not;

public class DispatcherServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(DispatcherServlet.class);

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
            var message = "Route %s not found".formatted(req.getRequestURI());
            resp.setCharacterEncoding(StandardCharsets.UTF_8);
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, message);
            return;
        }

        try {
            var methodInvocation = invokeMethod(destination, req);
            if (methodInvocation.success()) {
                resp.setCharacterEncoding(StandardCharsets.UTF_8);
                resp.setStatus(destination.statusCode());

                if (methodInvocation.resultAsOptional().isPresent()) {
                    resp.addHeader(Header.CONTENT_TYPE, destination.produces());
                    mapper.writeValue(resp.getOutputStream(), methodInvocation.result());
                }

            } else {
                var serverError = methodInvocation.resultAsOptional()
                        .filter(StringUtils::isString)
                        .map(result -> ServerError.internal((String) result))
                        .orElse(ServerError.internal());
                resp.setCharacterEncoding(StandardCharsets.UTF_8);
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, mapper.writeValueAsString(serverError));
            }
        } catch (RequiredRequestParameterMissingException ex) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
        }
    }

    private InvocationResult invokeMethod(RequestDestination destination, HttpServletRequest request) {
        return ReflectionUtil.invokeMethod(destination.method().instance(), getRequestParameterValues(destination.method().parameters(), request));
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
            var controllerInstance = ConstructorUtil.newInstance(controller, params).orElseThrow();

            var annotation = controller.getAnnotation(WebController.class);
            var basePath = annotation.path();

            for (Method method : controller.getMethods()) {
                if (method.isAnnotationPresent(GetMapping.class)) {
                    GetMapping getMapping = method.getAnnotation(GetMapping.class);

                    var methodPath = getMapping.path();
                    var route = Route.of(HttpMethod.GET, basePath, methodPath);

                    if (routes.containsKey(route)) {
                        throw DuplicateRouteException.forRoute(route);
                    }

                    var pathParameters = getPathParameters(route, method);

                    var requestMethod = RequestMethod.builder()
                            .name(method.getName())
                            .addParameters(pathParameters)
                            .addParameters(getQueryParameters(method))
                            .instance(ReflectionUtil.getBoundMethodHandle(controllerInstance, method))
                            .mediaType(MediaType.of(getMapping.produces()))
                            .status(getMapping.status())
                            .build();

                    validatePathVariables(route, pathParameters);

                    log.info("Found final path: {}", route);
                    routes.put(route, new RequestDestination(controller, requestMethod));
                } else if (method.isAnnotationPresent(PostMapping.class)) {
                    PostMapping postMapping = method.getAnnotation(PostMapping.class);

                    var methodPath = postMapping.path();
                    var route = Route.of(HttpMethod.POST, basePath, methodPath);

                    if (routes.containsKey(route)) {
                        throw DuplicateRouteException.forRoute(route);
                    }

                    var pathParameters = getPathParameters(route, method);
                    var requestMethod = RequestMethod.builder()
                            .name(method.getName())
                            .addParameters(pathParameters)
                            .addParameters(getQueryParameters(method))
                            .instance(ReflectionUtil.getBoundMethodHandle(controllerInstance, method))
                            .mediaType(MediaType.of(postMapping.produces()))
                            .status(postMapping.status())
                            .build();

                    validatePathVariables(route, pathParameters);

                    log.info("Found final path: {}", route);
                    routes.put(route, new RequestDestination(controller, requestMethod));
                }
            }
        }

        BeanRegistry.registerBean(CommonBeans.ROUTES_NAME, routes.keySet());
    }

    private Object[] getConstructorParamsForController(Class<?> controller) {
        return ConstructorUtil.getRequiredConstructorParameters(controller).stream()
                .map(BeanRegistry::findBeanByTypeOrThrow)
                .toArray();
    }

    private List<RequestParam.PathRequestParameter> getPathParameters(Route route, Method method) {
        return Arrays.stream(method.getParameters())
                .filter(parameter -> parameter.isAnnotationPresent(PathParam.class))
                .map(parameter -> {
                    var pathParam = parameter.getAnnotation(PathParam.class);
                    var pathParamName = StringUtils.hasText(pathParam.name()) ? pathParam.name() : parameter.getName();
                    var regex = route.path().replaceAll("\\{%s}".formatted(pathParamName), "([^/]+)");

                    return RequestParam.path(pathParamName, parameter.getType(), Pattern.compile(regex));
                })
                .toList();
    }

    private List<RequestParam.QueryRequestParameter> getQueryParameters(Method method) {
        return Arrays.stream(method.getParameters())
                .filter(parameter -> parameter.isAnnotationPresent(QueryParam.class))
                .map(parameter -> {
                    var queryParam = parameter.getAnnotation(QueryParam.class);
                    var queryParamName = StringUtils.hasText(queryParam.name()) ? queryParam.name() : parameter.getName();

                    return RequestParam.query(queryParamName, parameter.getType(), queryParam.required());
                })
                .toList();
    }

    private List<?> getRequestParameterValues(List<RequestParam> parameters, HttpServletRequest request) {
        return parameters.stream().map(parameter -> switch (parameter) {
            case RequestParam.QueryRequestParameter query -> getRequestParameterValue(query, request);
            case RequestParam.PathRequestParameter path -> getPathParameterValue(path, request);
        }).filter(Objects::nonNull).toList();
    }

    private Object getRequestParameterValue(RequestParam.QueryRequestParameter parameter, HttpServletRequest request) {
        var requestParameter = request.getParameter(parameter.name());

        if (requestParameter == null && parameter.required()) {
            throw RequiredRequestParameterMissingException.forParameter(parameter.name());
        }

        return StringUtils.parse(requestParameter, parameter.type());
    }

    private Object getPathParameterValue(RequestParam.PathRequestParameter parameter, HttpServletRequest request) {
        var matcher = parameter.capturePattern().matcher(request.getRequestURI());

        if (matcher.find()) {
            return StringUtils.parse(matcher.group(1), parameter.type());
        }

        throw RequiredRequestParameterMissingException.forParameter(parameter.name());
    }

    private void validatePathVariables(Route route, List<RequestParam.PathRequestParameter> parameters) {
        var configuredPathParameters = parameters.stream()
                .map(RequestParam::name)
                .toList();

        var unconfiguredPathParams = route.pathParams().stream()
                .filter(not(configuredPathParameters::contains))
                .toList();

        if (!unconfiguredPathParams.isEmpty()) {
            throw MissingPathParameterException.forPath(route, unconfiguredPathParams.getFirst());
        }
    }
}
