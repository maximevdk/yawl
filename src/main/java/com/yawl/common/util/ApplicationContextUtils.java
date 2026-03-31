package com.yawl.common.util;

import com.yawl.beans.ApplicationContext;
import jakarta.servlet.ServletContext;

/**
 * Utility methods for storing and retrieving the {@link ApplicationContext} from a {@link ServletContext}.
 */
public final class ApplicationContextUtils {
    private ApplicationContextUtils() {}

    private static final String APPLICATION_CONTEXT_NAME = "applicationContext";

    /**
     * Retrieves the {@link ApplicationContext} from the given servlet context.
     *
     * @param servletContext the servlet context
     * @return the application context
     */
    public static ApplicationContext getApplicationContext(ServletContext servletContext) {
        return (ApplicationContext) servletContext.getAttribute(APPLICATION_CONTEXT_NAME);
    }

    /**
     * Stores the {@link ApplicationContext} in the given servlet context.
     *
     * @param servletContext     the servlet context
     * @param applicationContext the application context to store
     */
    public static void setApplicationContext(ServletContext servletContext, ApplicationContext applicationContext) {
        servletContext.setAttribute(APPLICATION_CONTEXT_NAME, applicationContext);
    }
}
