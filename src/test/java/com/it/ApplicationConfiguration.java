package com.it;

import com.yawl.annotations.Bean;
import com.yawl.annotations.Configuration;
import com.yawl.database.InMemoryDatabase;
import tools.jackson.dataformat.yaml.YAMLMapper;

@Configuration
public class ApplicationConfiguration {
    @Bean(name = "pongDatabase")
    public InMemoryDatabase<String, Pong> pongDatabase() {
        return new InMemoryDatabase<>() {};
    }

    @Bean
    public YAMLMapper myYamlMapper() {
        return new YAMLMapper();
    }
}
