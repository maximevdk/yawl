package com.yawl;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.yawl.annotations.Service;
import com.yawl.beans.BeanRegistry;
import com.yawl.beans.CommonBeans;
import com.yawl.exception.InvalidContextException;
import com.yawl.util.ConstructorUtil;
import com.yawl.util.ReflectionUtil;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static com.yawl.util.StringUtil.decapitalize;

public class YawlApplication {
    private static final Logger log = LoggerFactory.getLogger(YawlApplication.class);

    public static void run(Class<?> baseClass, String... args) {
        var properties = init(baseClass);

        log.info("Starting YAWL Application {} in {} on port {}", properties.name(), properties.basePath(), properties.webConfig().port());

        var tomcat = new Tomcat();
        tomcat.setBaseDir("./target");
        tomcat.setPort(properties.webConfig().port());

        var context = tomcat.addContext(properties.webConfig().contextPath(), properties.basePath());

        tomcat.addServlet(properties.webConfig().contextPath(), "dispatcherServlet", BeanRegistry.findBeanByType(DispatcherServlet.class));
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

        ReflectionUtil reflectionUtil = new ReflectionUtil(baseClass.getPackage().getName());
        DispatcherServlet dispatcherServlet = new DispatcherServlet(jsonMapper, reflectionUtil);

        BeanRegistry.registerBean(CommonBeans.YAML_MAPPER_NAME, yamlMapper);
        BeanRegistry.registerBean(CommonBeans.JSON_MAPPER_NAME, jsonMapper);
        BeanRegistry.registerBean(CommonBeans.BASE_PACKAGE_NAME, baseClass.getPackage());
        BeanRegistry.registerBean(CommonBeans.BASE_PACKAGE_NAME_NAME, baseClass.getPackage().getName());
        BeanRegistry.registerBean(CommonBeans.REFLECTION_UTIL_NAME, reflectionUtil);
        BeanRegistry.registerBean(CommonBeans.DISPATCHER_SERVLET_NAME, dispatcherServlet);

        generateServiceBeans();

        try {
            var properties = yamlMapper.readValue(baseClass.getClassLoader().getResource("application.yml"), ApplicationProperties.class);
            return properties.application();
        } catch (IOException ex) {
            throw new InvalidContextException("Unable to find config file, no defaults have been implemented yet", ex);
        }
    }

    private static void generateServiceBeans() {
        var reflectionUtil = BeanRegistry.findBeanByType(ReflectionUtil.class);

        reflectionUtil.getClassesAnnotatedWith(Service.class)
                .forEach(clazz -> {
                            log.info("Creating bean for class {}", clazz);
                            ConstructorUtil.newInstance(clazz).ifPresent(instance -> BeanRegistry.registerBean(decapitalize(clazz.getSimpleName()), instance));
                        }
                );
    }
}
