package com.yawl.configuration;

import com.yawl.annotations.Bean;
import com.yawl.annotations.Configuration;
import com.yawl.beans.CommonBeans;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.dataformat.yaml.YAMLMapper;

@Configuration
public final class JacksonConfiguration {

    @Bean(name = CommonBeans.YAML_MAPPER_NAME)
    public YAMLMapper yamlMapper() {
        return YAMLMapper.builder()
                .propertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE)
                .build();
    }

    @Bean(name = CommonBeans.JSON_MAPPER_NAME)
    public JsonMapper jsonMapper() {
        return JsonMapper.builder().build();
    }
}
