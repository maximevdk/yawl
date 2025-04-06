package com.it;

import com.yawl.annotations.Bean;
import com.yawl.annotations.Configuration;
import com.yawl.database.InMemoryDatabase;

@Configuration
public class ApplicationConfiguration {
    @Bean(name = "pongDatabase")
    public InMemoryDatabase<String, Pong> pongDatabase() {
        return new InMemoryDatabase<>() {};
    }
}
