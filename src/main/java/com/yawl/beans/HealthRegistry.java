package com.yawl.beans;

import com.yawl.model.Health;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Thread-safe registry that tracks the application's overall {@link Health.Status}.
 */
public final class HealthRegistry {
    private static final AtomicReference<Health.Status> SYSTEM_STATUS = new AtomicReference<>(Health.Status.DOWN);

    /**
     * Marks the system as healthy and running.
     */
    public static void systemUp() {
        SYSTEM_STATUS.set(Health.Status.UP);
    }

    /**
     * Marks the system as starting up.
     */
    public static void systemStarting() {
        SYSTEM_STATUS.set(Health.Status.STARTING);
    }

    /**
     * Marks the system as down or unhealthy.
     */
    public static void systemDown() {
        SYSTEM_STATUS.set(Health.Status.DOWN);
    }

    /**
     * Returns the current system health status.
     *
     * @return the current health status
     */
    public static Health.Status systemStatus() {
        return SYSTEM_STATUS.get();
    }
}
