package com.yawl;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

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
