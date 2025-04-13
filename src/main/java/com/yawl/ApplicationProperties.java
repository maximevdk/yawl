package com.yawl;

import com.yawl.model.ManagementEndpointType;
import com.yawl.util.StringUtils;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

public record ApplicationProperties(Application application) {


    public record Application(String name, WebConfiguration webConfig, Management management) {
        /**
         * Get the applications path
         *
         * @return the base path were Tomcat should load the classes from.
         */
        public String basePath() {
            return new File(".").getAbsolutePath();
        }
    }

    record WebConfiguration(int port, String contextPath) {
    }

    record Management(ManagementEndpoint endpoint) {
        boolean managementEndpointEnabled() {
            return endpoint.enabled();
        }

        boolean endpointEnabled(ManagementEndpointType type) {
            return endpoint.includes().contains(type);
        }
    }

    record ManagementEndpoint(boolean enabled, String path, String include) {
        public List<ManagementEndpointType> includes() {
            if (!StringUtils.hasText(include)) {
                return List.of();
            }

            return Pattern.compile("\\s?,?\\s+").splitAsStream(include).map(ManagementEndpointType::of).toList();
        }
    }
}
