package com.yawl;

import java.io.File;

public record ApplicationProperties(Application application) {


    record Application(String name, WebConfiguration webConfig, Management management) {
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
    }

    record ManagementEndpoint(boolean enabled, String path) {
    }
}
