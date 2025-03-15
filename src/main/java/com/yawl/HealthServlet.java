package com.yawl;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.sun.management.OperatingSystemMXBean;
import com.yawl.beans.HealthRegistry;
import com.yawl.model.Header;
import com.yawl.model.Health;
import com.yawl.model.MediaType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.function.DoubleFunction;
import java.util.function.LongFunction;

public class HealthServlet extends HttpServlet {
    private final LongFunction<Long> TO_MB_FN = in -> in / (1024 * 1024);
    private final DoubleFunction<Double> TO_PERCENT_FN = in -> in * 100;
    private final JsonMapper mapper;

    public HealthServlet(JsonMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var runtime = Runtime.getRuntime();
        var os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        var health = Health.builder()
                .status(HealthRegistry.systemStatus())
                .availableMemory(TO_MB_FN.apply(runtime.freeMemory()))
                .totalMemory(TO_MB_FN.apply(runtime.totalMemory()))
                .cpuUsage(TO_PERCENT_FN.apply(os.getProcessCpuLoad()))
                .build();

        resp.setHeader(Header.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(resp.getOutputStream(), health);
    }
}
