package com.yawl.util;

import com.yawl.exception.NoSuchMethodException;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Set;

public final class ReflectionUtil {
    private static final Logger log = LoggerFactory.getLogger(ReflectionUtil.class);

    private static Reflections reflections = null;

    public ReflectionUtil(String basePackageName) {
        reflections = new Reflections(basePackageName);
    }

    public Set<Class<?>> getClassesAnnotatedWith(Class<? extends Annotation> annotationClass) {
        return reflections.getTypesAnnotatedWith(annotationClass);
    }

    public Object invokeMethodOnInstance(Object instance, String methodName, Object... args) {
        try {
            return instance.getClass().getMethod(methodName).invoke(instance, args);
        } catch (Exception ex) {
            log.error("Unable to invoke method {} on class {}", methodName, instance.getClass(), ex);
            throw NoSuchMethodException.forMethodNameAndClass(methodName, instance.getClass());
        }
    }
}
