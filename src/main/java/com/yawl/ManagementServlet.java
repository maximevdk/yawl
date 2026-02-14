package com.yawl;

import com.sun.management.OperatingSystemMXBean;
import com.yawl.annotations.EventListener;
import com.yawl.beans.HealthRegistry;
import com.yawl.events.ApplicationEvent;
import com.yawl.http.model.ContentType;
import com.yawl.http.model.Header;
import com.yawl.http.model.Route;
import com.yawl.model.Health;
import com.yawl.model.ManagementEndpointType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleFunction;
import java.util.function.LongFunction;

public class ManagementServlet extends HttpServlet {
    private static final LongFunction<Long> TO_MB_FN = in -> in / (1024 * 1024);
    private static final DoubleFunction<Double> TO_PERCENT_FN = in -> in * 100;

    private final ApplicationProperties.Application properties;
    private final JsonMapper mapper;
    private final Map<Route, Class<?>> routes;
    private final Map<String, Class<?>> beans;

    public ManagementServlet(ApplicationProperties.Application properties, JsonMapper mapper) {
        this.mapper = mapper;
        this.properties = properties;
        this.routes = new HashMap<>();
        this.beans = new HashMap<>();
    }

    @EventListener
    public void on(ApplicationEvent.RouteRegistryInitialized event) {
        event.routes().stream()
                .forEach(route -> routes.put(route.route(), route.method().getDeclaringClass()));
    }

    @EventListener
    public void on(ApplicationEvent.ApplicationContextRefreshed event) {
        beans.putAll(event.applicationContext().beansByName());
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var response = new HashMap<>();

        response.put("status", HealthRegistry.systemStatus());

        if (properties.management().endpointEnabled(ManagementEndpointType.HEALTH)) {
            response.put("health", getHealthInformation());
        }

        if (properties.management().endpointEnabled(ManagementEndpointType.DEBUG)) {
            response.put("debug", new Debug(beans, routes));
        }

        resp.setHeader(Header.CONTENT_TYPE, ContentType.APPLICATION_JSON_VALUE);
        mapper.writeValue(resp.getOutputStream(), response);
    }

    private Health getHealthInformation() {
        var runtime = Runtime.getRuntime();
        var os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        return Health.builder()
                .availableMemory(TO_MB_FN.apply(runtime.freeMemory()))
                .totalMemory(TO_MB_FN.apply(runtime.totalMemory()))
                .cpuUsage(TO_PERCENT_FN.apply(os.getProcessCpuLoad()))
                .build();
    }

    record Debug(Map<String, Class<?>> beans, Map<Route, ? extends Class<?>> routes) {
    }

}
