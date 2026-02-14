package com.yawl.test.extension;

import com.yawl.YawlApplication;
import com.yawl.annotations.Autowired;
import com.yawl.beans.ApplicationContext;
import com.yawl.test.annotation.YawlTest;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;
import java.util.Arrays;

public class YawlTestExtension implements BeforeEachCallback, BeforeAllCallback, AfterEachCallback, TestInstancePostProcessor {
    private static final String APPLICATION_CTX_KEY = "ctx";
    private static final String DEFAULT_CONFIG_LOCATION = "--config.location=defaults-yawl-test.yml";

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        if (contextNotInitialized(context)) {
            var ctx = YawlApplication.run(context.getRequiredTestClass(), DEFAULT_CONFIG_LOCATION);
            context.getStore(ExtensionContext.Namespace.GLOBAL).put(APPLICATION_CTX_KEY, ctx);
        }
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        var ctx = (ApplicationContext) context.getStore(ExtensionContext.Namespace.GLOBAL).get(APPLICATION_CTX_KEY);
        Arrays.stream(testInstance.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Autowired.class))
                .forEach(field -> setFieldValue(testInstance, field, ctx.getBeanByTypeOrThrow(field.getType())));
    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        var config = context.getRequiredTestClass().getAnnotation(YawlTest.class);

        if (config.dirtiesContext()) {
            context.getStore(ExtensionContext.Namespace.GLOBAL).remove(APPLICATION_CTX_KEY);
        }
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (contextNotInitialized(context)) {
            var ctx = YawlApplication.run(context.getRequiredTestClass(), DEFAULT_CONFIG_LOCATION);
            context.getStore(ExtensionContext.Namespace.GLOBAL).put(APPLICATION_CTX_KEY, ctx);
        }
    }

    private void setFieldValue(Object testInstance, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(testInstance, value);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Unable to set value for field: " + field.getName(), ex);
        }
    }

    private boolean contextNotInitialized(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.GLOBAL).get(APPLICATION_CTX_KEY) == null;
    }
}
