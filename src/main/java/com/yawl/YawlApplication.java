package com.yawl;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.yawl.beans.BeanLoader;
import com.yawl.exception.InvalidContextException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class YawlApplication {
    private static final Logger log = LoggerFactory.getLogger(YawlApplication.class);

    public static void run(Class<?> baseClass, String... args) {
        var properties = init(baseClass);

        log.info("Starting YAWL Application {} in {} on port {}", properties.name(), properties.basePath(), properties.webConfig().port());

        var tomcat = new Tomcat();
        tomcat.setBaseDir("./target");
        tomcat.setPort(properties.webConfig().port());

        var context = tomcat.addContext(properties.webConfig().contextPath(), properties.basePath());

        tomcat.addServlet(properties.webConfig().contextPath(), "dispatcherServlet", new DispatcherServlet());
        context.addServletMappingDecoded("/*", "dispatcherServlet");

        var connector = new Connector();
        connector.setPort(properties.webConfig().port());
        tomcat.setConnector(connector);

        try {
            tomcat.start();
        } catch (Exception ex) {
            throw new InvalidContextException("Unable to launch Tomcat", ex);
        }

        tomcat.getServer().await();
    }


    private static ApplicationProperties.Application init(Class<?> baseClass) {
        YAMLMapper yamlMapper = YAMLMapper.builder()
                .propertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE)
                .build();
        JsonMapper jsonMapper = JsonMapper.builder().build();

        BeanLoader.createBean("yamlMapper", yamlMapper);
        BeanLoader.createBean("jsonMapper", jsonMapper);
        BeanLoader.createBean("basePackage", baseClass.getPackage());
        BeanLoader.createBean("basePackageName", baseClass.getPackage().getName());

        try {
            var properties = yamlMapper.readValue(baseClass.getClassLoader().getResource("application.yml"), ApplicationProperties.class);
            return properties.application();
        } catch (IOException ex) {
            throw new InvalidContextException("Unable to find config file, no defaults have been implemented yet", ex);
        }
    }
}
