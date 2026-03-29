package com.yawl.configuration;

import com.yawl.ApplicationPropertiesInitializer;
import com.yawl.annotations.Bean;
import com.yawl.annotations.Configuration;
import tools.jackson.dataformat.yaml.YAMLMapper;

//TODO: add autowireable environment so that we can initialize ApplicationProperties here as well (it needs config file location property from arguments)
@Configuration
public class ApplicationPropertiesConfiguration {

    @Bean
    public ApplicationPropertiesInitializer initializer(YAMLMapper yamlMapper) {
        //todo: add qualifier so we make sure the yamlMapper injected is the one the framework initialized
        return new ApplicationPropertiesInitializer(yamlMapper);
    }
}
