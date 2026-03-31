package com.yawl.configuration;

import com.yawl.annotations.Configuration;
import com.yawl.annotations.Import;

/**
 * Aggregate configuration that imports the core framework configuration classes.
 */
@Configuration
@Import({JacksonConfiguration.class, ApplicationPropertiesConfiguration.class, HttpConfiguration.class})
public class CommonConfiguration {
}
