package com.yawl;

import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.dataformat.yaml.YAMLMapper;

public final class JacksonConfiguration {

    public static YAMLMapper buildYamlMapper() {
        return YAMLMapper.builder()
                .propertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE)
                .build();
    }

    public static JsonMapper buildJsonMapper() {
        return JsonMapper.builder().build();
    }
}
