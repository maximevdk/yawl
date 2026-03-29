package com.yawl.test.extension;

import com.yawl.WebServer;
import com.yawl.YawlApplication;
import com.yawl.annotations.Autowired;
import com.yawl.beans.ApplicationContext;
import com.yawl.beans.CommonBeans;
import com.yawl.test.annotation.LocalTestPort;
import com.yawl.test.annotation.YawlTest;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Optional;

public class YawlTestExtension implements BeforeEachCallback, BeforeAllCallback, AfterEachCallback, TestInstancePostProcessor, AfterAllCallback {
    private static final String APPLICATION_CTX_KEY = "ctx";
    private static final String DEFAULT_CONFIG_LOCATION = "--config.location=defaults-yawl-test.yml";

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        getContext(context).orElseGet(() -> initialize(context));
    }

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        var ctx = (ApplicationContext) context.getStore(ExtensionContext.Namespace.GLOBAL).get(APPLICATION_CTX_KEY);
        Arrays.stream(testInstance.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Autowired.class))
                .forEach(field -> setFieldValue(testInstance, field, ctx.getBeanByTypeOrThrow(field.getType())));

        if (ctx.containsBeanOfType(WebServer.class)) {
            var webserver = ctx.getBeanByNameOrThrow(CommonBeans.WEB_SERVER_NAME, WebServer.class);
            Arrays.stream(testInstance.getClass().getDeclaredFields())
                    .filter(field -> field.isAnnotationPresent(LocalTestPort.class))
                    .findFirst().ifPresent(field -> setFieldValue(testInstance, field, webserver.port()));
        }

    }

    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        var config = context.getRequiredTestClass().getAnnotation(YawlTest.class);

        if (config.dirtiesContext()) {
            //TODO: handle dirties context after each, without having to restart tomcat
        }
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        getContext(context).orElseGet(() -> initialize(context));
    }

    private void setFieldValue(Object testInstance, Field field, Object value) {
        try {
            field.setAccessible(true);
            field.set(testInstance, value);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Unable to set value for field: " + field.getName(), ex);
        }
    }

    private Optional<ApplicationContext> getContext(ExtensionContext context) {
        return Optional.ofNullable(context.getStore(ExtensionContext.Namespace.GLOBAL).get(APPLICATION_CTX_KEY, ApplicationContext.class));
    }

    private ApplicationContext initialize(ExtensionContext context) {
        var ctx = YawlApplication.run(context.getRequiredTestClass(), DEFAULT_CONFIG_LOCATION);
        context.getStore(ExtensionContext.Namespace.GLOBAL).put(APPLICATION_CTX_KEY, ctx);
        return ctx;
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        var ctx = context.getStore(ExtensionContext.Namespace.GLOBAL).remove(APPLICATION_CTX_KEY, ApplicationContext.class);
        if (ctx != null) {
            ctx.clear();
        }
    }
}
