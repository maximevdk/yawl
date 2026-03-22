package com.yawl;

import com.yawl.exception.InvalidContextException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.node.ObjectNode;
import tools.jackson.dataformat.yaml.YAMLMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ApplicationPropertiesInitializer {
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
        return classLoader.resources(".")
                .map(url -> new File(url.getPath()))
                .flatMap(file -> Optional.ofNullable(file.list()).stream().flatMap(Arrays::stream))
                .filter(fileName -> fileName.matches("application\\.(yml|yaml)"))
                .findFirst()
                .orElseThrow(() -> new FileNotFoundException("No user defined application.yml file found"));
    }
}
