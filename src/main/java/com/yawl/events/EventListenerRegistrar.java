package com.yawl.events;

/**
 * Registers event listener methods found on a bean instance.
 */
public interface EventListenerRegistrar {
    /**
     * Scans the given bean for {@link com.yawl.annotations.EventListener}-annotated methods and registers them.
     *
     * @param bean the bean to scan
     */
    void registerListeners(Object bean);
}
