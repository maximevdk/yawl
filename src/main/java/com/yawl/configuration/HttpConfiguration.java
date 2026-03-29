package com.yawl.configuration;

import com.yawl.annotations.Bean;
import com.yawl.annotations.Configuration;
import com.yawl.http.client.ApacheHttpExecutor;
import com.yawl.http.client.HttpExecutor;
import tools.jackson.databind.json.JsonMapper;

//TODO: enable only when @EnableHttpClient is active, otherwise this bean is not needed
@Configuration
public class HttpConfiguration {

    @Bean
    public HttpExecutor apacheHttpExecutor(JsonMapper jsonMapper) {
        return new ApacheHttpExecutor(jsonMapper);
    }
}
