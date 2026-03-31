package com.yawl.configuration;

import com.yawl.model.ManagementEndpointType;
import com.yawl.common.util.RegexUtil;
import com.yawl.common.util.StringUtils;

import java.io.File;
import java.util.List;

/**
 * Root configuration record that holds all application properties loaded from YAML configuration files.
 *
 * @param application the application-level configuration
 */
public record ApplicationProperties(Application application) {
    /**
     * Top-level application configuration including web server and management settings.
     *
     * @param name       the application name
     * @param web        the web server configuration
     * @param management the management endpoint configuration
     */
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

    /**
     * Web server toggle and configuration.
     *
     * @param enabled whether the web server is enabled
     * @param config  the web server configuration details
     */
    public record WebServer(boolean enabled, WebConfiguration config) {
    }

    /**
     * Web server configuration including port, context path, and virtual threads settings.
     *
     * @param port           the port the server listens on
     * @param contextPath    the servlet context path
     * @param virtualThreads virtual threads configuration
     */
    public record WebConfiguration(int port, String contextPath, VirtualThreadsConfiguration virtualThreads) {
    }

    /**
     * Virtual threads configuration for the embedded web server.
     *
     * @param enabled whether virtual threads are enabled
     * @param name    the thread name prefix
     */
    public record VirtualThreadsConfiguration(boolean enabled, String name) {
    }

    /**
     * Management configuration wrapping the management endpoint settings.
     *
     * @param endpoint the management endpoint configuration
     */
    public record Management(ManagementEndpoint endpoint) {
        /**
         * Returns whether the management endpoint is enabled.
         *
         * @return {@code true} if the management endpoint is enabled
         */
        public boolean managementEndpointEnabled() {
            return endpoint.enabled();
        }

        /**
         * Checks whether a specific management endpoint type is enabled.
         *
         * @param type the management endpoint type
         * @return {@code true} if the endpoint type is included in the configuration
         */
        public boolean endpointEnabled(ManagementEndpointType type) {
            return endpoint.includes().contains(type);
        }
    }

    /**
     * Management endpoint configuration including path and included endpoint types.
     *
     * @param enabled whether the management endpoint is enabled
     * @param path    the base path for management endpoints
     * @param include a comma-separated string of enabled endpoint types
     */
    public record ManagementEndpoint(boolean enabled, String path, String include) {
        /**
         * Parses the {@code include} string and returns the list of enabled management endpoint types.
         *
         * @return a list of enabled management endpoint types
         */
        public List<ManagementEndpointType> includes() {
            if (!StringUtils.hasText(include)) {
                return List.of();
            }

            return RegexUtil.enabledManagementEndpointsAsStream(include).map(ManagementEndpointType::of).toList();
        }
    }
}
