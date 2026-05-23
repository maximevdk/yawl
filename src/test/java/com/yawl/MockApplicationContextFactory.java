package com.yawl;

import com.yawl.beans.ApplicationContext;
import com.yawl.beans.model.CommonBeans;
import com.yawl.configuration.Environment;
import com.yawl.events.Event;
import com.yawl.events.EventListenerRegistrar;
import com.yawl.events.EventPublisher;

import java.util.List;

public final class MockApplicationContextFactory {

    public static ApplicationContext mockContext() {
        var context = new ApplicationContext();
        context.register(CommonBeans.ENVIRONMENT_NAME, new Environment(List.of()));
        context.register(CommonBeans.EVENT_PUBLISHER_NAME, new NoOpEventListenerRegistrar());
        return context;
    }

    static class NoOpEventListenerRegistrar implements EventPublisher, EventListenerRegistrar {
        @Override
        public void registerListeners(Object bean) {
        }

        @Override
        public void publish(Event event) {
        }
    }

}
