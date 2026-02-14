package com.yawl.events;

import com.yawl.beans.ApplicationContext;
import com.yawl.http.model.RegisteredRoute;

import java.util.List;

public sealed interface ApplicationEvent extends Event permits ApplicationEvent.ApplicationContextInitialized, ApplicationEvent.ApplicationContextRefreshed, ApplicationEvent.RouteRegistryInitialized {

    record ApplicationContextInitialized(ApplicationContext applicationContext) implements ApplicationEvent {
    }

    record ApplicationContextRefreshed(ApplicationContext applicationContext) implements ApplicationEvent {
    }

    record RouteRegistryInitialized(List<RegisteredRoute> routes) implements ApplicationEvent {
    }

}
