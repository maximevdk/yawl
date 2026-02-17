package com.yawl.events;

import com.yawl.annotations.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public final class EventRegistry implements EventPublisher, EventListenerRegistrar {
    private static final Logger log = LoggerFactory.getLogger(EventRegistry.class);

    private final Map<Class<? extends Event>, List<Consumer<? extends Event>>> listeners = new ConcurrentHashMap<>();

    @Override
    public void publish(Event event) {
        listeners.getOrDefault(event.getClass(), List.of())
                .forEach(listener -> ((Consumer<Event>) listener).accept(event));
    }

    public <E extends Event> void register(Class<E> eventType, Consumer<E> listener) {
        listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    @Override
    public void registerListeners(Object bean) {
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
                register((Class<? extends Event>) parameter.getType(), event -> {
                    try {
                        method.invoke(bean, event);
                    } catch (Exception ex) {
                        // error while invoking listener
                        log.error("Error while invoking @EventListener: {}", method.getName(), ex);
                    }
                });
            }
        }
    }
}
