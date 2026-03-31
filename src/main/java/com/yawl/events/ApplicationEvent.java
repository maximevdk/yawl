package com.yawl.events;

import com.yawl.beans.ApplicationContext;
import com.yawl.http.model.RegisteredRoute;

import java.util.List;

/**
 * Sealed hierarchy of application lifecycle events published during startup and initialization.
 */
public sealed interface ApplicationEvent extends Event permits ApplicationEvent.ApplicationContextInitialized, ApplicationEvent.ApplicationContextRefreshed, ApplicationEvent.RouteRegistryInitialized {

    /**
     * Published when the {@link ApplicationContext} has been created and core beans are registered.
     *
     * @param applicationContext the initialized application context
     */
    record ApplicationContextInitialized(ApplicationContext applicationContext) implements ApplicationEvent {
    }

    /**
     * Published after all user-defined beans have been loaded and the context is fully refreshed.
     *
     * @param applicationContext the refreshed application context
     */
    record ApplicationContextRefreshed(ApplicationContext applicationContext) implements ApplicationEvent {
    }

    /**
     * Published after the route registry has been populated with all discovered routes.
     *
     * @param routes the list of registered routes
     */
    record RouteRegistryInitialized(List<RegisteredRoute> routes) implements ApplicationEvent {
    }

}
