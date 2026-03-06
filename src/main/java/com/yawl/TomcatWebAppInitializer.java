package com.yawl;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class TomcatWebAppInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("TomcatWebAppInitializer contextInitialized");
    }
}
