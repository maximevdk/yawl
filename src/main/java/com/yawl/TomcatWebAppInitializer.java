package com.yawl;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet context listener that handles web application initialization events.
 */
public class TomcatWebAppInitializer implements ServletContextListener {
    private static final Logger log = LoggerFactory.getLogger(TomcatWebAppInitializer.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //TODO: use this in the future when needed
        log.trace("TomcatWebAppInitializer contextInitialized");
    }
}
