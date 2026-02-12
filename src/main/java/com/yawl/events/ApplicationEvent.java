package com.yawl.events;

import com.yawl.beans.ApplicationContext;
import com.yawl.http.model.RegisteredRoute;

import java.util.List;

import static com.yawl.events.ApplicationEvent.ApplicationContextInitialized;
import static com.yawl.events.ApplicationEvent.RouteRegistryInitialized;

public sealed interface ApplicationEvent extends Event permits ApplicationContextInitialized, RouteRegistryInitialized {

    record ApplicationContextInitialized(ApplicationContext applicationContext) implements ApplicationEvent {
    }

    record RouteRegistryInitialized(List<RegisteredRoute> routes) implements ApplicationEvent {
    }

}
