package com.yawl.configuration;

import com.yawl.ApplicationPropertiesInitializer;
import com.yawl.annotations.Bean;
import com.yawl.annotations.Configuration;
import com.yawl.annotations.Qualifier;
import com.yawl.beans.model.CommonBeans;
import com.yawl.configuration.model.CommonProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.dataformat.yaml.YAMLMapper;

@Configuration
public class ApplicationPropertiesConfiguration {
    private static final Logger log = LoggerFactory.getLogger(ApplicationPropertiesConfiguration.class);

    @Bean
    public ApplicationPropertiesInitializer initializer(@Qualifier(CommonBeans.YAML_MAPPER_NAME) YAMLMapper yamlMapper) {
        //todo: add qualifier so we make sure the yamlMapper injected is the one the framework initialized
        return new ApplicationPropertiesInitializer(yamlMapper);
    }

    @Bean(name = CommonBeans.APPLICATION_PROPERTIES_NAME)
    public ApplicationProperties.Application systemApplicationProperties(ApplicationPropertiesInitializer initializer, Environment environment) {
        var defaultConfigLocation = environment.getProperty(CommonProperties.OVERWRITE_DEFAULTS_CONFIG_LOCATION, "defaults.yml");

        log.debug("Using default configuration location: {}", defaultConfigLocation);
        return initializer.init(defaultConfigLocation);
    }
}
