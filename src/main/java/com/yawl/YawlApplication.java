package com.yawl;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.yawl.annotations.Repository;
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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

import static com.yawl.util.StringUtil.decapitalize;

public class YawlApplication {
    private static final String TOMCAT_DIRECTORY = "./target/temp";
    private static final Logger log = LoggerFactory.getLogger(YawlApplication.class);

    public static void run(Class<?> baseClass, String... args) {
        var properties = init(baseClass);

        log.info("Starting YAWL Application {} in {} on port {}", properties.name(), properties.basePath(), properties.webConfig().port());

        var tomcat = new Tomcat();
        tomcat.setBaseDir(TOMCAT_DIRECTORY);
        tomcat.setPort(properties.webConfig().port());

        var context = tomcat.addContext(properties.webConfig().contextPath(), properties.basePath());
        context.addLifecycleListener(new TomcatLifecycleListener());

        tomcat.addServlet(properties.webConfig().contextPath(), "dispatcherServlet", BeanRegistry.findBeanByType(DispatcherServlet.class));
        context.addServletMappingDecoded("/*", "dispatcherServlet");

        if (properties.management().managementEndpointEnabled()) {
            tomcat.addServlet(properties.webConfig().contextPath(), "healthServlet", BeanRegistry.findBeanByType(HealthServlet.class));
            context.addServletMappingDecoded(properties.management().endpoint().path(), "healthServlet");
        }

        var connector = new Connector();
        connector.setPort(properties.webConfig().port());
        tomcat.setConnector(connector);

        try {
            tomcat.start();
        } catch (Exception ex) {
            throw new InvalidContextException("Unable to launch Tomcat", ex);
        }

        configureRuntimeHooks();

        tomcat.getServer().await();
    }


    private static ApplicationProperties.Application init(Class<?> baseClass) {
        YAMLMapper yamlMapper = JacksonConfiguration.buildYamlMapper();
        JsonMapper jsonMapper = JacksonConfiguration.buildJsonMapper();

        ReflectionUtil reflectionUtil = new ReflectionUtil(baseClass.getPackage().getName());
        DispatcherServlet dispatcherServlet = new DispatcherServlet(jsonMapper, reflectionUtil);
        HealthServlet healthServlet = new HealthServlet(jsonMapper);

        BeanRegistry.registerBean(CommonBeans.YAML_MAPPER_NAME, yamlMapper);
        BeanRegistry.registerBean(CommonBeans.JSON_MAPPER_NAME, jsonMapper);
        BeanRegistry.registerBean(CommonBeans.BASE_PACKAGE_NAME, baseClass.getPackage());
        BeanRegistry.registerBean(CommonBeans.BASE_PACKAGE_NAME_NAME, baseClass.getPackage().getName());
        BeanRegistry.registerBean(CommonBeans.REFLECTION_UTIL_NAME, reflectionUtil);
        BeanRegistry.registerBean(CommonBeans.DISPATCHER_SERVLET_NAME, dispatcherServlet);
        BeanRegistry.registerBean(CommonBeans.HEALTH_SERVLET_NAME, healthServlet);

        findAndRegisterBeans(reflectionUtil);

        return initializeApplicationProperties(yamlMapper, baseClass).application();
    }

    private static void findAndRegisterBeans(ReflectionUtil reflectionUtil) {
        //TODO: refactor that it doesn't matter in which order those are initialized
        reflectionUtil.getClassesAnnotatedWith(Repository.class)
                .forEach(YawlApplication::registerBean);
        reflectionUtil.getClassesAnnotatedWith(Service.class)
                .forEach(YawlApplication::registerBean);
    }

    private static void registerBean(Class<?> clazz) {
        log.info("Creating bean for class {}", clazz);
        var dependencies = ConstructorUtil.getRequiredConstructorParameters(clazz).stream()
                .map(BeanRegistry::findBeanByType)
                .toArray();
        ConstructorUtil.newInstance(clazz, dependencies)
                .ifPresent(instance -> BeanRegistry.registerBean(decapitalize(clazz.getSimpleName()), instance));
    }

    private static ApplicationProperties initializeApplicationProperties(YAMLMapper mapper, Class<?> baseClass) {
        try {
            return mapper.readValue(baseClass.getClassLoader().getResourceAsStream("application.yml"), ApplicationProperties.class);
        } catch (Exception ex) {
            throw new InvalidContextException("Unable to find config file, no defaults have been implemented yet", ex);
        }
    }

    private static void configureRuntimeHooks() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try (var files = Files.walk(Paths.get(TOMCAT_DIRECTORY))) {
                files.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);

                log.debug("Tomcat directory {} deleted successfully.", TOMCAT_DIRECTORY);
            } catch (Exception ex) {
                //ignore
            }
        }));
    }
}
