package com.yawl.events;

public class EventPublisher {

    private final EventBus eventBus;

    public EventPublisher(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    public void publish(Event event) {
        eventBus.putEvent(event);
    }
}
