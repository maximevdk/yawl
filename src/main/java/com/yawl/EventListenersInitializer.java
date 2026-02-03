package com.yawl;

import com.yawl.annotations.EventListener;
import com.yawl.events.Event;
import com.yawl.events.EventBus;
import com.yawl.util.ConstructorUtil;
import com.yawl.util.ReflectionUtil;

import java.lang.reflect.Method;
import java.util.HashMap;

public class EventListenersInitializer {

    public static EventBus createEventBus() {
        var eventBus = new EventBus();
        var instanceCache = new HashMap<Class<?>, Object>();
        var methods = ReflectionUtil.getMethodsAnnotatedWith(EventListener.class);

        for (Method method : methods) {
            if (method.getParameterCount() != 1) {
                throw new IllegalArgumentException("Methods annotated with @EventListener must have exactly 1 parameter");
            }

            var parameter = method.getParameters()[0];
            if (!Event.class.isAssignableFrom(parameter.getType())) {
                throw new IllegalArgumentException("Method parameters of @EventListener's must implement Event");
            }

            var instance = instanceCache.compute(method.getDeclaringClass(), (key, value) -> {
                if (value == null) {
                    return ConstructorUtil.newInstance(key).orElseThrow();
                }

                return value;
            });
            var methodHandle = ReflectionUtil.getBoundMethodHandle(instance, method);
            eventBus.registerListener((Class<? extends Event>) parameter.getType(), methodHandle);
        }

        return eventBus;
    }
}
