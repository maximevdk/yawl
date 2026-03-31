package com.yawl;

import com.yawl.beans.ApplicationContext;

/**
 * Abstraction for an embedded web server that hosts the application.
 */
public interface WebServer {
    /**
     * Starts the web server with the given application context.
     *
     * @param applicationContext the application context
     */
    void start(ApplicationContext applicationContext);

    /**
     * Stops the web server and releases resources.
     */
    void stop();

    /**
     * Returns whether the web server is currently running.
     *
     * @return {@code true} if running
     */
    boolean isRunning();

    /**
     * Returns the port the web server is listening on.
     *
     * @return the server port
     */
    int port();
}
