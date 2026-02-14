package com.yawl.events;

import com.yawl.annotations.EventListener;

import java.lang.reflect.Method;

public class EventListenerRegistrar {
    private final EventRegistry eventRegistry;

    public EventListenerRegistrar(EventRegistry eventRegistry) {
        this.eventRegistry = eventRegistry;
    }

    public void registerBean(Object bean) {
        for (Method method : bean.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventListener.class)) {
                if (method.getParameterCount() != 1) {
                    throw new IllegalArgumentException("Methods annotated with @EventListener must have exactly 1 parameter");
                }

                var parameter = method.getParameters()[0];
                if (!Event.class.isAssignableFrom(parameter.getType())) {
                    throw new IllegalArgumentException("Method parameters of @EventListener's must implement Event");
                }

                method.setAccessible(true);
                eventRegistry.register(parameter.getType(), event -> {
                    try {
                        method.invoke(bean, event);
                    } catch (Exception ex) {
                        throw new RuntimeException("Failed to invoke listener", ex);
                    }
                });
            }
        }
    }
}
