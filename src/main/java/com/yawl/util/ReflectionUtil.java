package com.yawl.util;

import com.yawl.exception.NoSuchMethodException;
import com.yawl.exception.NotInitializedException;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

public final class ReflectionUtil {
    private static final Logger log = LoggerFactory.getLogger(ReflectionUtil.class);

    private static Reflections reflections = null;

    private ReflectionUtil() {
    }

    public static Set<Class<?>> getClassesAnnotatedWith(Class<? extends Annotation> annotationClass) {
        if (reflections == null) {
            throw new NotInitializedException("Call ReflectionUtil.init() before using this method");
        }

        return reflections.getTypesAnnotatedWith(annotationClass);
    }

    public static Set<Method> getMethodsAnnotatedWith(Class<? extends Annotation> annotationClass) {
        if (reflections == null) {
            throw new NotInitializedException("Call ReflectionUtil.init() before using this method");
        }

        return reflections.getMethodsAnnotatedWith(annotationClass);
    }

    public static Object invokeMethodOnInstance(Object instance, String methodName, Object... args) {
        try {
            var parameterClassTypes = Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);
            return instance.getClass().getMethod(methodName, parameterClassTypes).invoke(instance, args);
        } catch (Exception ex) {
            log.error("Unable to invoke method {} on class {}", methodName, instance.getClass(), ex);
            throw NoSuchMethodException.forMethodNameAndClass(methodName, instance.getClass());
        }
    }

    public static void init(String basePackageName) {
        reflections = new Reflections(basePackageName);
    }
}
