package com.yawl.util;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.Set;

public final class ReflectionUtil {
    private static final Logger log = LoggerFactory.getLogger(ReflectionUtil.class);
    private static ReflectionUtil INSTANCE;
    private static Reflections reflections = null;

    private ReflectionUtil(String basePackageName) {
        reflections = new Reflections(basePackageName);
    }

    public Set<Class<?>> getClassesAnnotatedWith(Class<? extends Annotation> annotationClass) {
        return reflections.getTypesAnnotatedWith(annotationClass);
    }


    public static ReflectionUtil instance(String basePackageName) {
        return new ReflectionUtil(basePackageName);
    }

}
