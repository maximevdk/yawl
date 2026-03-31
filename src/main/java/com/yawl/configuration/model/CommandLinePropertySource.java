package com.yawl.configuration.model;

import com.yawl.common.util.RegexUtil;

import java.util.Map;

/**
 * A {@link PropertySource} backed by command-line arguments parsed as key-value pairs.
 */
public class CommandLinePropertySource implements PropertySource {
    private final Map<String, String> properties;

    private CommandLinePropertySource(Map<String, String> properties) {
        this.properties = properties;
    }

    /**
     * Creates a new property source by parsing the given command-line arguments.
     *
     * @param args the command-line arguments
     * @return a new command-line property source
     */
    public static CommandLinePropertySource from(String... args) {
        return new CommandLinePropertySource(RegexUtil.parseCommandLineArguments(args));
    }

    @Override
    public String getProperty(String key) {
        return properties.get(key);
    }
}
