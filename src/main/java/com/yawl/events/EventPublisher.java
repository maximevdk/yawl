package com.yawl.events;

public interface EventPublisher<E extends Event> {
    void publish(E event);
}
