package com.yawl;

import com.yawl.beans.ApplicationContext;
import com.yawl.beans.CommonBeans;
import com.yawl.exception.InvalidContextException;
import org.apache.catalina.LifecycleException;
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

public final class TomcatWebServer {
    private static final Logger log = LoggerFactory.getLogger(TomcatWebServer.class);
    private static final String TOMCAT_DIRECTORY = "./target/temp";

    private final ApplicationContext applicationContext;

    public TomcatWebServer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Tomcat start() {
        var properties = applicationContext.getBeanByNameOrThrow(CommonBeans.APPLICATION_PROPERTIES_NAME, ApplicationProperties.Application.class);
        var config = properties.web().config();
        var tomcat = new Tomcat();
        tomcat.setBaseDir(TOMCAT_DIRECTORY);
        tomcat.setPort(config.port());

        var context = tomcat.addContext(config.contextPath(), properties.basePath());
        context.addLifecycleListener(new TomcatLifecycleListener());
        context.addServletContainerInitializer(new DefaultServletContainerInitializer(applicationContext), Set.of());

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
            log.info("Starting YAWL Application {} in {} on port {}", properties.name(), properties.basePath(), config.port());

            var beforeStartTime = System.currentTimeMillis();
            tomcat.start();
            var afterStartTime = System.currentTimeMillis();
            log.info("Tomcat started on path {} took {} ms", tomcat.getServer().getCatalinaBase(), afterStartTime - beforeStartTime);
        } catch (Exception ex) {
            log.error("Unable to launch Tomcat server", ex);
            throw new InvalidContextException("Unable to launch Tomcat", ex);
        }

        configureShutdownHook(tomcat);
        return tomcat;
    }

    private static void configureShutdownHook(Tomcat tomcat) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
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
        }));
    }
}
