package com.yawl.configuration.model;

import com.yawl.common.util.RegexUtil;

import java.util.Map;

public class CommandLinePropertySource implements PropertySource {
    private final Map<String, String> properties;

    private CommandLinePropertySource(Map<String, String> properties) {
        this.properties = properties;
    }

    public static CommandLinePropertySource from(String... args) {
        return new CommandLinePropertySource(RegexUtil.parseCommandLineArguments(args));
    }


    @Override
    public String getProperty(String key) {
        return properties.get(key);
    }
}
