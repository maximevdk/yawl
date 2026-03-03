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
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
                return HttpResponse.noContent(destination.responseInfo().status());
            } else {
                return HttpResponse.notFound("Entity not found");
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            return HttpResponse.internal(e.getMessage());
        }
    }

    private Object[] getParameterValues(HttpServletRequest req, RegisteredRoute destination) {
        var pathParams = destination.route().extractVariables(req.getRequestURI());
        var parameters = new ArrayList<>();
        for (Parameter parameter : destination.method().getParameters()) {
            if (parameter.isAnnotationPresent(QueryParam.class)) {
                var queryParam = parameter.getAnnotation(QueryParam.class);
                var name = queryParam.name() != null ? queryParam.name() : parameter.getName();
                parameters.add(StringUtils.parse(req.getParameterValues(name), parameter.getParameterizedType()));
            } else if (parameter.isAnnotationPresent(PathParam.class)) {
                var pathParam = parameter.getAnnotation(PathParam.class);
                Optional.ofNullable(pathParams.get(pathParam.name()))
                        .ifPresentOrElse(parameters::add, () -> MissingPathParameterException.forPath(destination.route(), pathParam.name()));
            } else {
                throw MissingRequiredParameterException.of(parameter.getName());
            }
        }

        return parameters.toArray();
    }

    private void writeResponse(ResponseInfo info, HttpResponse response, HttpServletResponse resp) throws IOException {
        resp.setStatus(response.status().getCode());

        if (response instanceof HttpResponse.NoContent) {
            return;
        }

        resp.setCharacterEncoding(StandardCharsets.UTF_8);
        resp.setContentType(info.contentType().value());

        var body = jsonMapper.writeValueAsBytes(response);
        resp.setContentLength(body.length);
        resp.getOutputStream().write(body);
    }
}
