package com.yawl.test.extension;

import com.yawl.BeanCreationService;
import com.yawl.JacksonConfiguration;
import com.yawl.annotations.Autowired;
import com.yawl.beans.BeanRegistry;
import com.yawl.beans.CommonBeans;
import com.yawl.test.annotation.YawlTest;
import com.yawl.util.ReflectionUtil;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;
import java.util.Arrays;

public class YawlTestExtension implements BeforeEachCallback, BeforeAllCallback, AfterEachCallback, TestInstancePostProcessor {
    private static final String INITIALIZED_KEY = "initialized";

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        if (contextNotInitialized(context)) {
            ReflectionUtil.init(context.getRequiredTestClass().getPackageName());
            BeanRegistry.registerBean(CommonBeans.JSON_MAPPER_NAME, JacksonConfiguration.buildJsonMapper());
            BeanRegistry.registerBean(CommonBeans.YAML_MAPPER_NAME, JacksonConfiguration.buildYamlMapper());

            new BeanCreationService().findAndRegisterBeans();
            setContextInitialized(context);
        }
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        Arrays.stream(testInstance.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Autowired.class))
                .peek(field -> field.setAccessible(true))
                .forEach(field -> setFieldValue(testInstance, field));
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        var config = context.getRequiredTestClass().getAnnotation(YawlTest.class);

        if (config.dirtiesContext()) {
            BeanRegistry.clear();
            context.getStore(ExtensionContext.Namespace.GLOBAL).remove(INITIALIZED_KEY);
        }
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (contextNotInitialized(context)) {
            ReflectionUtil.init(context.getRequiredTestClass().getPackageName());
            BeanRegistry.registerBean(CommonBeans.JSON_MAPPER_NAME, JacksonConfiguration.buildJsonMapper());
            BeanRegistry.registerBean(CommonBeans.YAML_MAPPER_NAME, JacksonConfiguration.buildYamlMapper());

            new BeanCreationService().findAndRegisterBeans();
            setContextInitialized(context);
        }
    }

    private void setFieldValue(Object testInstance, Field field) {
        try {
            field.set(testInstance, BeanRegistry.findBeanByTypeOrThrow(field.getType()));
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Unable to set value for field: " + field.getName(), ex);
        }
    }

    private boolean contextNotInitialized(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.GLOBAL).get(INITIALIZED_KEY) == null;
    }

    private void setContextInitialized(ExtensionContext context) {
        context.getStore(ExtensionContext.Namespace.GLOBAL).put(INITIALIZED_KEY, true);
    }
}
