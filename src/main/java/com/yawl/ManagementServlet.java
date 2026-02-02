package com.yawl;

import com.sun.management.OperatingSystemMXBean;
import com.yawl.beans.ApplicationContext;
import com.yawl.beans.CommonBeans;
import com.yawl.beans.HealthRegistry;
import com.yawl.model.Header;
import com.yawl.model.Health;
import com.yawl.model.ManagementEndpointType;
import com.yawl.model.MediaType;
import com.yawl.model.Route;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.DoubleFunction;
import java.util.function.LongFunction;

public class ManagementServlet extends HttpServlet {
    private final LongFunction<Long> TO_MB_FN = in -> in / (1024 * 1024);
    private final DoubleFunction<Double> TO_PERCENT_FN = in -> in * 100;
    private final ApplicationContext applicationContext;
    private final ApplicationProperties.Application properties;
    private final JsonMapper mapper;

    public ManagementServlet(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.mapper = applicationContext.getBeanByNameOrThrow(CommonBeans.JSON_MAPPER_NAME, JsonMapper.class);
        this.properties = applicationContext.getBeanByNameOrThrow(CommonBeans.APPLICATION_PROPERTIES_NAME, ApplicationProperties.Application.class);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var response = new HashMap<>();

        response.put("status", HealthRegistry.systemStatus());

        if (properties.management().endpointEnabled(ManagementEndpointType.HEALTH)) {
            response.put("health", getHealthInformation());
        }

        if (properties.management().endpointEnabled(ManagementEndpointType.DEBUG)) {
            response.put("debug", getDebugInformation());
        }

        resp.setHeader(Header.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
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

    private Debug getDebugInformation() {
        var beans = new HashMap<>(applicationContext.beans());
        //TODO: fix routes
        return new Debug(beans, Set.of());
    }

    record Debug(Map<String, Class<?>> beans, Set<Route> routes) {
    }

}
