package com.yawl;

import com.yawl.beans.HealthRegistry;
import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Catalina lifecycle event listener class.
 * <p>
 * Listens to the following <a href="https://tomcat.apache.org/tomcat-8.0-doc/api/constant-values.html#org.apache.catalina.Lifecycle.AFTER_INIT_EVENT">events</a>.
 */
public class TomcatLifecycleListener implements LifecycleListener {
    private static final Logger log = LoggerFactory.getLogger(TomcatLifecycleListener.class);

    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        log.trace("Received event {} lifecycle {}", event.getType(), event.getLifecycle());

        switch (event.getType()) {
            case Lifecycle.BEFORE_INIT_EVENT -> HealthRegistry.systemStarting();
            case Lifecycle.AFTER_START_EVENT -> HealthRegistry.systemUp();
            case Lifecycle.BEFORE_STOP_EVENT -> HealthRegistry.systemDown();
        }
    }
}
