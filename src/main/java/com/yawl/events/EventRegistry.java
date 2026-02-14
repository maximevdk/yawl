package com.yawl.events;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class EventRegistry<E extends Event> implements EventPublisher<E> {
    private final Map<Class<E>, List<Consumer<E>>> listeners = new ConcurrentHashMap<>();

    @Override
    public void publish(E event) {
        listeners.getOrDefault(event.getClass(), List.of())
                .forEach(listener -> listener.accept(event));
    }

    public void register(Class<E> eventType, Consumer<E> listener) {
        listeners.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(listener);
    }
}
