package com.yawl.events;

import com.yawl.beans.ApplicationContext;

import static com.yawl.events.ApplicationEvent.*;

public sealed interface ApplicationEvent extends Event permits ApplicationContextInitialized {

    record ApplicationContextInitialized(ApplicationContext applicationContext) implements ApplicationEvent {
    }

}
