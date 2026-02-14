package com.yawl;

import com.yawl.annotations.PathParam;
import com.yawl.annotations.QueryParam;
import com.yawl.beans.ApplicationContext;
import com.yawl.beans.CommonBeans;
import com.yawl.events.ApplicationEvent;
import com.yawl.events.EventPublisher;
import com.yawl.exception.MissingPathParameterException;
import com.yawl.exception.MissingRequiredParameterException;
import com.yawl.exception.RouteNotFoundException;
import com.yawl.http.RouteRegistry;
import com.yawl.http.model.HttpResponse;
import com.yawl.http.model.RegisteredRoute;
import com.yawl.http.model.ResponseInfo;
import com.yawl.util.StringUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;

public class DispatcherServlet extends HttpServlet {

    private final JsonMapper jsonMapper;
    private final ApplicationContext applicationContext;
    private final RouteRegistry routeRegistry;

    public DispatcherServlet(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.jsonMapper = applicationContext.getBeanByNameOrThrow(CommonBeans.JSON_MAPPER_NAME);
        this.routeRegistry = new RouteRegistry();
    }

    @Override
    public void init() throws ServletException {
        routeRegistry.init(applicationContext);
        applicationContext.getBeanByTypeOrThrow(EventPublisher.class)
                .publish(new ApplicationEvent.RouteRegistryInitialized(routeRegistry.getRoutes()));
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var destination = routeRegistry.find(req.getMethod(), req.getRequestURI())
                .orElseThrow(() -> RouteNotFoundException.notFound(req.getMethod(), req.getRequestURI()));

        var response = invokeRoute(req, destination);
        writeResponse(destination.responseInfo(), response, resp);
    }


    private HttpResponse invokeRoute(HttpServletRequest req, RegisteredRoute destination) {
        try {
            var method = destination.method();
            var parameters = getParameterValues(req, destination);

            var controllerInstance = applicationContext.getBeanByTypeOrThrow(method.getDeclaringClass());
            var result = method.invoke(controllerInstance, parameters);

            if (result != null) {
                return HttpResponse.ok(result, destination.responseInfo().status());
            } else if (method.getReturnType() == void.class) {
                return HttpResponse.ok(destination.responseInfo().status());
            } else {
                return HttpResponse.notFound("Entity not found");
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            return HttpResponse.internal(e.getMessage());
        }
    }

    private Object[] getParameterValues(HttpServletRequest req, RegisteredRoute destination) {
        var pathParams = destination.route().extractVariables(req.getRequestURI());
        return Arrays.stream(destination.method().getParameters())
                .map(parameter -> {
                    if (parameter.isAnnotationPresent(QueryParam.class)) {
                        var queryParam = parameter.getAnnotation(QueryParam.class);
                        var name = queryParam.name() != null ? queryParam.name() : parameter.getName();
                        return StringUtils.parse(req.getParameter(name), parameter.getType());
                    } else if (parameter.isAnnotationPresent(PathParam.class)) {
                        var pathParam = parameter.getAnnotation(PathParam.class);
                        return Optional.ofNullable(pathParams.get(pathParam.name()))
                                .orElseThrow(() -> MissingPathParameterException.forPath(destination.route(), pathParam.name()));
                    } else {
                        throw MissingRequiredParameterException.of(parameter.getName());
                    }
                })
                .toArray();
    }

    private void writeResponse(ResponseInfo info, HttpResponse response, HttpServletResponse resp) throws IOException {
        var body = jsonMapper.writeValueAsBytes(response);
        resp.setCharacterEncoding(StandardCharsets.UTF_8);
        resp.setContentType(info.contentType().value());
        resp.setStatus(response.status().getCode());
        resp.setContentLength(body.length);
        resp.getOutputStream().write(body);
    }
}
