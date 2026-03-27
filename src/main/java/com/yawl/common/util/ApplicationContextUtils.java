package com.yawl.common.util;

import com.yawl.beans.ApplicationContext;
import jakarta.servlet.ServletContext;

public final class ApplicationContextUtils {
    private static final String APPLICATION_CONTEXT_NAME = "applicationContext";

    public static ApplicationContext getApplicationContext(ServletContext servletContext) {
        return (ApplicationContext) servletContext.getAttribute(APPLICATION_CONTEXT_NAME);
    }

    public static void setApplicationContext(ServletContext servletContext, ApplicationContext applicationContext) {
        servletContext.setAttribute(APPLICATION_CONTEXT_NAME, applicationContext);
    }
}
