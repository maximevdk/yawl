package com.yawl.model;

import com.yawl.beans.ApplicationContext;

public sealed interface ApplicationEvent extends Event {

    record ApplicationContextInitializedEvent(ApplicationContext applicationContext) implements ApplicationEvent { }

}
