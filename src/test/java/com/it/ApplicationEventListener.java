package com.it;

import com.yawl.annotations.EventListener;
import com.yawl.annotations.Service;
import com.yawl.events.ApplicationEvent;

@Service
public class ApplicationEventListener {
    @EventListener
    public void on(ApplicationEvent.ApplicationContextInitialized event) {
        System.out.println("ApplicationContext Initialized");
    }
}
