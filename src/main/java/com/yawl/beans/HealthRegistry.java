package com.yawl.beans;

import com.yawl.model.Health;

import java.util.concurrent.atomic.AtomicReference;

public final class HealthRegistry {
    public static AtomicReference<Health.Status> SYSTEM_STATUS = new AtomicReference<>(Health.Status.DOWN);

    public static void systemUp() {
        SYSTEM_STATUS.set(Health.Status.UP);
    }

    public static void systemStarting() {
        SYSTEM_STATUS.set(Health.Status.STARTING);
    }

    public static void systemDown() {
        SYSTEM_STATUS.set(Health.Status.DOWN);
    }

    public static Health.Status systemStatus() {
        return SYSTEM_STATUS.get();
    }
}
