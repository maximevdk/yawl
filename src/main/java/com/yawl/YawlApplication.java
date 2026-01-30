package com.yawl;

import com.yawl.beans.BeanRegistry;
import com.yawl.beans.CommonBeans;
import com.yawl.exception.InvalidContextException;
import com.yawl.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.dataformat.yaml.YAMLMapper;

public class YawlApplication {
    private static final Logger log = LoggerFactory.getLogger(YawlApplication.class);

    public static void run(Class<?> baseClass, String... args) {
        var properties = init(baseClass);
        var application = properties.application();
        new BeanCreationService().findAndRegisterBeans();

        log.info("Starting YAWL Application {} in {} on port {}", application.name(), application.basePath(), application.webConfig().port());

        var tomcat = TomcatWebServer.start(properties);
        //this looks like it should be the last command, other commands are not getting executed before shutdown is called
        tomcat.getServer().await();
    }


    private static ApplicationProperties init(Class<?> baseClass) {
        var yamlMapper = JacksonConfiguration.buildYamlMapper();
        var jsonMapper = JacksonConfiguration.buildJsonMapper();
        var properties = initializeApplicationProperties(yamlMapper, baseClass);
        ReflectionUtil.init(baseClass.getPackage().getName());

        BeanRegistry.registerBean(CommonBeans.APPLICATION_PROPERTIES_NAME, properties.application());
        BeanRegistry.registerBean(CommonBeans.YAML_MAPPER_NAME, yamlMapper);
        BeanRegistry.registerBean(CommonBeans.JSON_MAPPER_NAME, jsonMapper);
        BeanRegistry.registerBean(CommonBeans.BASE_PACKAGE_NAME, baseClass.getPackage());
        BeanRegistry.registerBean(CommonBeans.BASE_PACKAGE_NAME_NAME, baseClass.getPackage().getName());

        return properties;
    }

    private static ApplicationProperties initializeApplicationProperties(YAMLMapper mapper, Class<?> baseClass) {
        try {
            return mapper.readValue(baseClass.getClassLoader().getResourceAsStream("application.yml"), ApplicationProperties.class);
        } catch (Exception ex) {
            throw new InvalidContextException("Unable to find config file, no defaults have been implemented yet", ex);
        }
    }
}
