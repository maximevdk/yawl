package com.yawl.model;

import com.yawl.util.RegexUtil;
import com.yawl.util.StringUtils;

import java.io.File;
import java.util.List;

public record ApplicationProperties(Application application) {
    public record Application(String name, WebServer web, Management management) {
        /**
         * Get the applications path
         *
         * @return the base path were Tomcat should load the classes from.
         */
        public String basePath() {
            return new File(".").getAbsolutePath();
        }
    }

    public record WebServer(boolean enabled, WebConfiguration config) {
    }

    public record WebConfiguration(int port, String contextPath) {
    }

    public record Management(ManagementEndpoint endpoint) {
        public boolean managementEndpointEnabled() {
            return endpoint.enabled();
        }

        public boolean endpointEnabled(ManagementEndpointType type) {
            return endpoint.includes().contains(type);
        }
    }

    public record ManagementEndpoint(boolean enabled, String path, String include) {
        public List<ManagementEndpointType> includes() {
            if (!StringUtils.hasText(include)) {
                return List.of();
            }

            return RegexUtil.enabledManagementEndpointsAsStream(include).map(ManagementEndpointType::of).toList();
        }
    }
}
