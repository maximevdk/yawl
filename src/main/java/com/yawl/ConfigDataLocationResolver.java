package com.yawl;

import com.yawl.configuration.ConfigurableEnvironment;
import com.yawl.configuration.model.CommonProperties;
import com.yawl.configuration.model.YamlConfigurationFilePropertySource;

import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Objects;
import java.util.Set;

class ConfigDataLocationResolver {
    private static final Set<String> USER_CONFIG_FILE_NAMES = Set.of("application.yml", "application.yaml");

    public void applyTo(ConfigurableEnvironment environment) {
        try {
            var classLoader = Thread.currentThread().getContextClassLoader();
            var defaultEnvironmentLocation = environment.getProperty(CommonProperties.OVERWRITE_DEFAULTS_CONFIG_LOCATION).orElse("defaults.yml");

            environment.addPropertySource(YamlConfigurationFilePropertySource.init(userConfigFileLocation(classLoader).openStream()));
            environment.addPropertySource(YamlConfigurationFilePropertySource.init(classLoader.getResourceAsStream(defaultEnvironmentLocation)));
        } catch (Exception ex) {
            throw new RuntimeException("Unable to read config file.", ex);
        }
    }

    private URL userConfigFileLocation(ClassLoader classLoader) throws FileNotFoundException {
        return USER_CONFIG_FILE_NAMES.stream()
                .map(classLoader::getResource)
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> new FileNotFoundException("No user defined application.yml file found"));
    }
}
