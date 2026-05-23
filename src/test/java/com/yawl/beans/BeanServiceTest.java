package com.yawl.beans;

import com.yawl.MockApplicationContextFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class BeanServiceTest {
    private final BeanService service = new BeanService(MockApplicationContextFactory.mockContext());

    @Test
    void loadAndInitializeConfig_notAConfigClass_throws() {
        assertThrows(IllegalArgumentException.class, () -> service.loadAndInitializeConfig(String.class));
    }
}