package com.yawl;

import com.yawl.beans.ApplicationContext;
import com.yawl.configuration.ApplicationProperties;
import com.yawl.exception.InvalidContextException;
import com.yawl.common.util.ApplicationContextUtils;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardVirtualThreadExecutor;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Set;

public final class TomcatWebServer implements WebServer {
    private static final Logger log = LoggerFactory.getLogger(TomcatWebServer.class);
    private static final String TOMCAT_DIRECTORY = "./target/temp";
    private final Tomcat tomcat;

    public TomcatWebServer() {
        tomcat = new Tomcat();
        tomcat.setBaseDir(TOMCAT_DIRECTORY);
    }

    @Override
    public void start(ApplicationContext applicationContext) {
        var properties = applicationContext.getBeanByTypeOrThrow(ApplicationProperties.Application.class);
        var config = properties.web().config();
        tomcat.setPort(config.port());

        var context = tomcat.addContext(config.contextPath(), properties.basePath());
        ApplicationContextUtils.setApplicationContext(context.getServletContext(), applicationContext);
        context.addLifecycleListener(new TomcatLifecycleListener());
        context.addServletContainerInitializer(new DefaultServletContainerInitializer(), Set.of());

        var connector = new Connector();
        connector.setPort(config.port());
        connector.setURIEncoding("UTF-8");
        //TODO: add property for this?
        connector.setProperty("gracefulShutdown", "true");

        //enable virtual threads when enabled
        if (config.virtualThreads().enabled()) {
            var executor = new StandardVirtualThreadExecutor();
            executor.setName("virtualThreadExecutor");
            executor.setNamePrefix(config.virtualThreads().name());
            tomcat.getService().addExecutor(executor);
            connector.getProtocolHandler().setExecutor(executor);
        }

        tomcat.setConnector(connector);

        try {
            log.info("Starting YAWL Application {} in {} on port {}", properties.name(), properties.basePath(), tomcat.getConnector().getLocalPort());

            var beforeStartTime = System.currentTimeMillis();
            tomcat.start();
            var afterStartTime = System.currentTimeMillis();
            log.info("Tomcat started on path {} took {} ms", tomcat.getServer().getCatalinaBase(), afterStartTime - beforeStartTime);
        } catch (Exception ex) {
            log.error("Unable to launch Tomcat server", ex);
            throw new InvalidContextException("Unable to launch Tomcat", ex);
        }

        configureShutdownHook();
    }

    @Override
    public void stop() {
        try {
            tomcat.stop();
            tomcat.destroy();
        } catch (LifecycleException ex) {
            //ignore
            log.error("Error stopping Tomcat", ex);
        }

        try (var files = Files.walk(Paths.get(TOMCAT_DIRECTORY))) {
            files.sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            log.info("Tomcat directory {} deleted successfully.", tomcat.getServer().getCatalinaBase());
        } catch (Exception ex) {
            // ignore
        }
    }

    @Override
    public boolean isRunning() {
        return tomcat.getServer().getState() == LifecycleState.STARTED;
    }

    @Override
    public int port() {
        return tomcat.getConnector().getLocalPort();
    }

    Tomcat getTomcat() {
        return tomcat;
    }

    private void configureShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }
}
