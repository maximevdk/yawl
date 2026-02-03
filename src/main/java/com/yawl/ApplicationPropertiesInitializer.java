package com.yawl;

import com.yawl.exception.InvalidContextException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.dataformat.yaml.YAMLMapper;

import java.util.Map;

public class ApplicationPropertiesInitializer {
    private final YAMLMapper mapper;

    public ApplicationPropertiesInitializer(YAMLMapper mapper) {
        this.mapper = mapper;
    }

    public ApplicationProperties.Application init(String defaultConfigLocation) {
        try {
            var defaultProperties = (ObjectNode) mapper.readTree(YawlApplication.class.getClassLoader().getResourceAsStream(defaultConfigLocation));
            var overwrites = (ObjectNode) mapper.readTree(YawlApplication.class.getClassLoader().getResourceAsStream("application.yml"));
            merge(overwrites, defaultProperties);
            return mapper.treeToValue(defaultProperties, ApplicationProperties.class).application();
        } catch (Exception ex) {
            throw new InvalidContextException("Unable to find config file, no defaults have been implemented yet", ex);
        }
    }

    private static void merge(ObjectNode overwrites, ObjectNode defaultProperties) {
        for (Map.Entry<String, JsonNode> property : overwrites.properties()) {
            if (property.getValue().isValueNode()) {
                defaultProperties.set(property.getKey(), property.getValue());
                continue;
            }

            merge((ObjectNode) property.getValue(), (ObjectNode) defaultProperties.get(property.getKey()));
        }
    }
}
