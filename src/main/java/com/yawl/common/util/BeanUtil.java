package com.yawl.common.util;

import com.yawl.annotations.HttpClient;
import com.yawl.annotations.Qualifier;
import com.yawl.annotations.Repository;
import com.yawl.annotations.Service;
import com.yawl.annotations.WebController;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.yawl.common.util.StringUtils.decapitalize;

/**
 * Utility methods for determining bean names and checking whether a class qualifies as a bean.
 */
public final class BeanUtil {
    private BeanUtil() {
    }

    private static final List<Class<? extends Annotation>> ENABLED_ANNOTATIONS = List.of(HttpClient.class, Service.class, Repository.class, WebController.class);

    /**
     * Checks whether the given class is annotated with a recognized bean stereotype annotation.
     *
     * @param clazz the class to check
     * @return {@code true} if the class carries a known bean annotation
     */
    public static boolean isBean(Class<?> clazz) {
        return Arrays.stream(clazz.getAnnotations())
                .map(Annotation::annotationType)
                .anyMatch(ENABLED_ANNOTATIONS::contains);
    }

    /**
     * Resolves the bean name for the given class, using the annotation's {@code name} attribute if present,
     * or falling back to the decapitalized simple class name.
     *
     * @param clazz the class to resolve the name for
     * @return the bean name
     */
    public static String getBeanName(Class<?> clazz) {
        if (!isBean(clazz)) {
            return decapitalize(clazz.getSimpleName());
        }

        return ENABLED_ANNOTATIONS.stream()
                .map(clazz::getAnnotation)
                .filter(Objects::nonNull)
                .findFirst()
                .map(BeanUtil::getBeanName)
                .filter(StringUtils::hasText)
                .orElse(decapitalize(clazz.getSimpleName()));
    }

    /**
     * Returns the bean name for a constructor or method parameter, using the {@link Qualifier} value if present,
     * or the parameter name otherwise.
     *
     * @param parameter the parameter to inspect
     * @return the resolved parameter name
     */
    public static String getParameterName(Parameter parameter) {
        if (parameter.isAnnotationPresent(Qualifier.class)) {
            return parameter.getAnnotation(Qualifier.class).value();
        }

        return parameter.getName();
    }

    private static String getBeanName(Annotation annotation) {
        return switch (annotation) {
            case HttpClient client -> client.name();
            case Service service -> service.name();
            case Repository repo -> repo.name();
            default -> null;
        };
    }
}
