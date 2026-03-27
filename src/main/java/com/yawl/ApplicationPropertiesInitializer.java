package com.yawl;

import com.yawl.configuration.ApplicationProperties;
import com.yawl.exception.InvalidContextException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.dataformat.yaml.YAMLMapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

public class ApplicationPropertiesInitializer {
    private static final Set<String> CONFIG_FILE_NAMES = Set.of("application.yml", "application.yaml");
    private final YAMLMapper mapper;

    public ApplicationPropertiesInitializer(YAMLMapper mapper) {
        this.mapper = mapper;
    }

    public ApplicationProperties.Application init(String defaultConfigLocation) {
        try {
            var defaultProperties = (ObjectNode) mapper.readTree(streamResource(defaultConfigLocation));
            var overwrites = (ObjectNode) mapper.readTree(streamResource(userConfigFileLocation()));
            merge(overwrites, defaultProperties);
            return mapper.treeToValue(defaultProperties, ApplicationProperties.class).application();
        } catch (IOException ex) {
            throw new InvalidContextException("Unable to read config file.", ex);
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

    private InputStream streamResource(String resourceLocation) throws IOException {
        var resourceUrl = Thread.currentThread().getContextClassLoader().getResource(resourceLocation);

        if (resourceUrl == null) {
            throw new FileNotFoundException("Unable to load property file %s".formatted(resourceLocation));
        }

        return resourceUrl.openStream();
    }

    private String userConfigFileLocation() throws FileNotFoundException {
        var classLoader = Thread.currentThread().getContextClassLoader();
        return CONFIG_FILE_NAMES.stream()
                .filter(name -> classLoader.getResource(name) != null)
                .findFirst()
                .orElseThrow(() -> new FileNotFoundException("No user defined application.yml file found"));
    }
}
