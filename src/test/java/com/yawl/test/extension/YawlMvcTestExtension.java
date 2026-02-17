package com.yawl.test.extension;

import com.yawl.annotations.Autowired;
import com.yawl.beans.ApplicationContext;
import com.yawl.test.annotation.YawlMvcTest;
import com.yawl.test.beans.TestContext;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class YawlMvcTestExtension implements BeforeEachCallback, BeforeAllCallback, AfterEachCallback, TestInstancePostProcessor, AfterAllCallback {
    private static final String APPLICATION_CTX_KEY = "ctx";
    private static final String DEFAULT_CONFIG_LOCATION = "defaults-yawl-mvc-test.yml";

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        if (contextNotInitialized(context)) {
            var yawlMvcTest = context.getRequiredTestClass().getAnnotation(YawlMvcTest.class);
            var includes = new HashSet<Class<?>>(1);
            includes.add(yawlMvcTest.controller());
            includes.addAll(List.of(yawlMvcTest.imports()));

            var ctx = new TestContext().buildTestContext(includes, DEFAULT_CONFIG_LOCATION);
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
        var config = context.getRequiredTestClass().getAnnotation(YawlMvcTest.class);

        if (config.dirtiesContext()) {
            var ctx = context.getStore(ExtensionContext.Namespace.GLOBAL).remove(APPLICATION_CTX_KEY, ApplicationContext.class);
            if (ctx != null) {
                ctx.clear();
            }
        }
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        if (contextNotInitialized(context)) {
            var yawlMvcTest = context.getRequiredTestClass().getAnnotation(YawlMvcTest.class);
            var includes = new HashSet<Class<?>>(1);
            includes.add(yawlMvcTest.controller());
            includes.addAll(List.of(yawlMvcTest.imports()));

            var ctx = new TestContext().buildTestContext(includes, DEFAULT_CONFIG_LOCATION);
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

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        var ctx = context.getStore(ExtensionContext.Namespace.GLOBAL).remove(APPLICATION_CTX_KEY, ApplicationContext.class);
        if (ctx != null) {
            ctx.clear();
        }
    }
}
