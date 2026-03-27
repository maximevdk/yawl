package com.yawl;

import com.yawl.beans.ApplicationContext;
import com.yawl.common.HttpResponseWriter;
import com.yawl.common.JsonHttpResponseWriter;
import com.yawl.common.util.ApplicationContextUtils;
import com.yawl.events.ApplicationEvent;
import com.yawl.events.EventPublisher;
import com.yawl.exception.ExceptionResolver;
import com.yawl.exception.RouteNotFoundException;
import com.yawl.exception.WebApplicationExceptionHandler;
import com.yawl.http.HttpServletRequestWriter;
import com.yawl.http.RouteRegistry;
import com.yawl.http.ServletRequestArgumentResolver;
import com.yawl.http.model.ContentType;
import com.yawl.http.model.HttpResponse;
import com.yawl.http.model.RegisteredRoute;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

public class DispatcherServlet extends HttpServlet {
    private static final Logger log = LoggerFactory.getLogger(DispatcherServlet.class);
    private final RouteRegistry routeRegistry = new RouteRegistry();
    private WebApplicationExceptionHandler exceptionHandler;
    private ApplicationContext applicationContext;
    private ServletRequestArgumentResolver argumentResolver;
    private HttpServletRequestWriter httpServletRequestWriter;

    @Override
    public void init() throws ServletException {
        applicationContext = ApplicationContextUtils.getApplicationContext(getServletContext());
        exceptionHandler = new WebApplicationExceptionHandler(applicationContext.findBeansByType(ExceptionResolver.class));
        argumentResolver = applicationContext.getBeanByTypeOrThrow(ServletRequestArgumentResolver.class);
        httpServletRequestWriter = new HttpServletRequestWriter(applicationContext.findBeansByType(HttpResponseWriter.class),
                applicationContext.getBeanByTypeOrThrow(JsonHttpResponseWriter.class));


        routeRegistry.init(applicationContext);
        applicationContext.getBeanByTypeOrThrow(EventPublisher.class)
                .publish(new ApplicationEvent.RouteRegistryInitialized(routeRegistry.getRoutes()));
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            var destination = routeRegistry.find(req.getMethod(), req.getRequestURI())
                    .orElseThrow(() -> RouteNotFoundException.notFound(req.getMethod(), req.getRequestURI()));

            var response = invokeRoute(req, destination);
            writeResponse(destination.responseInfo().contentType(), response, resp);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);

            var response = exceptionHandler.handle(ex);
            writeResponse(ContentType.APPLICATION_JSON, response, resp);
        }
    }

    private HttpResponse<?> invokeRoute(HttpServletRequest req, RegisteredRoute destination) throws Exception {
        var method = destination.method();
        var parameters = Arrays.stream(destination.method().getParameters())
                .map(parameter -> argumentResolver.resolveParameter(req, destination.route(), parameter))
                .toArray();

        var controllerInstance = applicationContext.getBeanByTypeOrThrow(method.getDeclaringClass());
        var result = method.invoke(controllerInstance, parameters);

        if (result != null) {
            return HttpResponse.ok(result, destination.responseInfo().status());
        } else if (method.getReturnType() == void.class) {
            return HttpResponse.noContent(destination.responseInfo().status());
        } else {
            return HttpResponse.notFound("Entity not found");
        }
    }

    private void writeResponse(ContentType contentType, HttpResponse<?> response, HttpServletResponse resp) throws IOException {
        resp.setStatus(response.status().getCode());

        if (response instanceof HttpResponse.NoContent) {
            return;
        }

        httpServletRequestWriter.write(response, contentType, resp);
    }
}
