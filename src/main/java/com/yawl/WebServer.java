package com.yawl;

import com.yawl.beans.ApplicationContext;

public interface WebServer {
    void start(ApplicationContext applicationContext);

    void stop();

    boolean isRunning();

    int port();
}
