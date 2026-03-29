package com.yawl.configuration;

import com.yawl.annotations.Configuration;
import com.yawl.annotations.Import;

@Configuration
@Import({JacksonConfiguration.class, ApplicationPropertiesConfiguration.class, HttpConfiguration.class})
public class CommonConfiguration {
}
