package com.yawl;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.sun.management.OperatingSystemMXBean;
import com.yawl.beans.BeanRegistry;
import com.yawl.beans.HealthRegistry;
import com.yawl.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.function.DoubleFunction;
import java.util.function.LongFunction;

public class ManagementServlet extends HttpServlet {
    private final LongFunction<Long> TO_MB_FN = in -> in / (1024 * 1024);
    private final DoubleFunction<Double> TO_PERCENT_FN = in -> in * 100;
    private final ApplicationProperties.Application properties;
    private final JsonMapper mapper;

    public ManagementServlet(JsonMapper mapper, ApplicationProperties.Application properties) {
        this.mapper = mapper;
        this.properties = properties;
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
        return new Debug(BeanRegistry.getBeans(), Map.of());
    }
}
