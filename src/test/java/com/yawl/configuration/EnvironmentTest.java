package com.yawl.configuration;

import com.yawl.MapPropertySource;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EnvironmentTest {

    private final Environment environment = new Environment(List.of(
            new MapPropertySource(Map.of("key1", "value1", "key2", "value2")),
            new MapPropertySource(Map.of("key1", "valueShouldNotOverwrite1", "key3", "999", "key4", "true"))
    ));

    @Test
    void getProperty() {
        //should take the first value it finds, so value1
        assertEquals("value1", environment.getProperty("key1", null));
        assertEquals("999", environment.getProperty("key3", null));
        assertEquals("111", environment.getProperty("unknown", "111"));
    }

    @Test
    void getProperty_byType() {
        assertEquals(999, environment.getProperty("key3", null, Integer.class));
        assertEquals(111, environment.getProperty("unknown", 111, Integer.class));
        assertEquals(true, environment.getProperty("key4", null, boolean.class));
    }

    @Test
    void containsPropertyWithValue() {
        assertTrue(environment.containsPropertyWithValue("key1", "value1"));
        assertFalse(environment.containsPropertyWithValue("key2", "value1"));

        assertFalse(environment.containsPropertyWithValue("unknown", "value1"));
        assertTrue(environment.containsPropertyWithValue("unknown", null));

        assertTrue(environment.containsPropertyWithValue(null, null));
    }
}