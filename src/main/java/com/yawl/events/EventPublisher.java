package com.yawl.events;

import com.yawl.annotations.EventListener;

public class EventPublisher {

    void publish(Event event) {
    }

    @EventListener
    public void on(ApplicationEvent.ApplicationContextInitialized event) {
        System.out.println("ApplicationContext Initialized");
    }
}
