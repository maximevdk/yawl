package com.yawl.configuration;

import com.yawl.annotations.Bean;
import com.yawl.annotations.Configuration;
import com.yawl.beans.model.CommonBeans;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.dataformat.yaml.YAMLMapper;

@Configuration
public final class JacksonConfiguration {
    /** Creates a new instance. */
    public JacksonConfiguration() {}

    /**
     * Creates the YAML mapper bean configured with kebab-case naming strategy.
     *
     * @return a new YAML mapper
     */
    @Bean(name = CommonBeans.YAML_MAPPER_NAME)
    public YAMLMapper yamlMapper() {
        return YAMLMapper.builder()
                .propertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE)
                .build();
    }

    /**
     * Creates the JSON mapper bean.
     *
     * @return a new JSON mapper
     */
    @Bean(name = CommonBeans.JSON_MAPPER_NAME)
    public JsonMapper jsonMapper() {
        return JsonMapper.builder().build();
    }
}
