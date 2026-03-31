package com.yawl.events;

/**
 * Publishes events to all registered listeners.
 */
public interface EventPublisher {
    /**
     * Publishes the given event to all matching listeners.
     *
     * @param event the event to publish
     */
    void publish(Event event);
}
