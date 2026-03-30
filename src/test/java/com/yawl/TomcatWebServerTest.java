package com.yawl;

import com.yawl.beans.ApplicationContext;
import com.yawl.beans.model.CommonBeans;
import com.yawl.configuration.ApplicationProperties;
import com.yawl.configuration.ApplicationProperties.Application;
import com.yawl.configuration.ApplicationProperties.Management;
import com.yawl.configuration.ApplicationProperties.ManagementEndpoint;
import com.yawl.configuration.ApplicationProperties.WebConfiguration;
import com.yawl.configuration.ApplicationProperties.VirtualThreadsConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TomcatWebServerTest {
    private TomcatWebServer server;
    private ApplicationContext context;

    @BeforeEach
    void setUp() {
        server = new TomcatWebServer();
        context = new ApplicationContext();

        var properties = new Application(
                "test-app",
                new ApplicationProperties.WebServer(true,
                        new WebConfiguration(0, "",
                                new VirtualThreadsConfiguration(false, ""))),
                new Management(
                        new ManagementEndpoint(false, "", ""))
        );
        context.register(CommonBeans.APPLICATION_PROPERTIES_NAME, properties);
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void startAndStop() {
        server.start(context);

        assertThat(server.isRunning()).isTrue();
        assertThat(server.port()).isGreaterThan(0);

        server.stop();

        assertThat(server.isRunning()).isFalse();
    }
}
