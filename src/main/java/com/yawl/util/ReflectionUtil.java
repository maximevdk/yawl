package com.yawl.util;

import com.yawl.annotations.ExtendedBy;
import com.yawl.exception.NotInitializedException;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class ReflectionUtil {
    private static final Logger log = LoggerFactory.getLogger(ReflectionUtil.class);
    private static Reflections reflections = null;

    private ReflectionUtil() {
    }

    public static Set<Class<?>> getClassesAnnotatedWith(Class<? extends Annotation> annotationClass) {
        if (notInitialized()) {
            throw new NotInitializedException("Call ReflectionUtil.init() before using this method");
        }

        var annotations = new ArrayList<Class<? extends Annotation>>(List.of(annotationClass));

        if (annotationClass.isAnnotationPresent(ExtendedBy.class)) {
            annotations.addAll(List.of(annotationClass.getAnnotation(ExtendedBy.class).value()));
        }

        return annotations.stream()
                .map(reflections::getTypesAnnotatedWith)
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
    }

    public static <T> Optional<T> invoke(Method method, Object instance, List<?> arguments) {
        try {
            if (method.getReturnType() == void.class) {
                throw new IllegalArgumentException("Method returns void");
            }

            return Optional.ofNullable((T) method.invoke(instance, arguments.toArray()));
        } catch (Exception ex) {
            log.error("Error invoking method {} on class {}", method.getName(), instance.getClass(), ex);
            return Optional.empty();
        }
    }

    public static void init(Class<?> baseClass) {
        var config = new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forClass(baseClass))
                .setScanners(Scanners.TypesAnnotated)
                .filterInputsBy(new FilterBuilder().includePackage(baseClass.getPackageName()));

        reflections = new Reflections(config);
    }

    public static boolean notInitialized() {
        return reflections == null;
    }
}
