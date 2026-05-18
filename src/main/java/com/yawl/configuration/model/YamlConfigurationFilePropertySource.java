package com.yawl.configuration.model;

import com.yawl.common.util.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class YamlConfigurationFilePropertySource implements PropertySource {
    private final Map<String, String> properties;

    private YamlConfigurationFilePropertySource(Map<String, String> properties) {
        this.properties = properties;
    }

    public static YamlConfigurationFilePropertySource init(InputStream in) {
        var properties = load(in).entrySet().stream().map(YamlConfigurationFilePropertySource::getFinalProperties)
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return new YamlConfigurationFilePropertySource(properties);
    }

    private static Map<String, Object> load(InputStream in) {
        var yaml = new Yaml();
        return yaml.load(in);
    }

    private static Map<String, String> getFinalProperties(Map.Entry<String, Object> entry) {
        if (Map.class.isAssignableFrom(entry.getValue().getClass())) {
            var properties = new HashMap<String, String>();

            var values = (Map<String, Object>) entry.getValue();
            for (Map.Entry<String, Object> childEntry : values.entrySet()) {
                properties.putAll(getFinalProperties(Map.entry(entry.getKey() + ".", childEntry)));
            }

            return properties;
        }

        if (Map.Entry.class.isAssignableFrom(entry.getValue().getClass())) {
            var valueEntry = (Map.Entry<String, Object>) entry.getValue();
            if (Map.class.isAssignableFrom(valueEntry.getValue().getClass())) {
                var values = (Map<String, Object>) valueEntry.getValue();
                return getFinalProperties(Map.entry(entry.getKey() + valueEntry.getKey(), values));
            }

            return Map.of(entry.getKey() + valueEntry.getKey(), StringUtils.toString(valueEntry.getValue()));
        }

        return Map.of(entry.getKey(), StringUtils.toString(entry.getValue()));
    }


    @Override
    public String getProperty(String key) {
        return properties.get(key);
    }
}
