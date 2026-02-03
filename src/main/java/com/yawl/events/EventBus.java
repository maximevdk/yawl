package com.yawl.events;

import java.lang.invoke.MethodHandle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EventBus {
    private final Map<Class<? extends Event>, List<MethodHandle>> listeners;

    public EventBus() {
        listeners = new ConcurrentHashMap<>();
    }

    public void putEvent(Event event) {
        var methodHandles = listeners.getOrDefault(event.getClass(), List.of());

        for (MethodHandle listener : methodHandles) {
            try {
                listener.invokeWithArguments(event);
            } catch (Throwable e) {
                //ignore we can add retry later
            }
        }
    }

    public void registerListener(Class<? extends Event> event, MethodHandle methodHandle) {
        listeners.compute(event, (key, value) -> {
            if (value == null) {
                return new ArrayList<>(List.of(methodHandle));
            } else {
                value.add(methodHandle);
                return value;
            }
        });
    }
}
