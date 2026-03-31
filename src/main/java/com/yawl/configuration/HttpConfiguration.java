package com.yawl.configuration;

import com.yawl.annotations.Bean;
import com.yawl.annotations.Configuration;
import com.yawl.annotations.Qualifier;
import com.yawl.beans.model.CommonBeans;
import com.yawl.http.client.ApacheHttpExecutor;
import com.yawl.http.client.HttpExecutor;
import tools.jackson.databind.json.JsonMapper;

//TODO: enable only when @EnableHttpClient is active, otherwise this bean is not needed
@Configuration
public class HttpConfiguration {

    /**
     * Creates the default {@link HttpExecutor} bean backed by Apache HttpClient.
     *
     * @param jsonMapper the JSON mapper for serialization
     * @return a new Apache HTTP executor
     */
    @Bean
    public HttpExecutor apacheHttpExecutor(@Qualifier(CommonBeans.JSON_MAPPER_NAME) JsonMapper jsonMapper) {
        return new ApacheHttpExecutor(jsonMapper);
    }
}
