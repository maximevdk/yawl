package com.yawl.util;

import com.yawl.annotations.Repository;
import com.yawl.annotations.Service;
import com.yawl.exception.NotABeanException;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.yawl.util.StringUtils.decapitalize;

public final class BeanUtil {
    private static final List<Class<? extends Annotation>> ENABLED_ANNOTATIONS = List.of(Service.class, Repository.class);

    public static boolean isBean(Class<?> clazz) {
        return Arrays.stream(clazz.getAnnotations())
                .map(Annotation::annotationType)
                .anyMatch(ENABLED_ANNOTATIONS::contains);
    }

    public static String getBeanName(Class<?> clazz) {
        if (!isBean(clazz)) {
            throw NotABeanException.forClass(clazz);
        }

        return ENABLED_ANNOTATIONS.stream()
                .map(clazz::getAnnotation)
                .filter(Objects::nonNull)
                .findFirst()
                .map(BeanUtil::getBeanName)
                .filter(StringUtils::hasText)
                .orElse(decapitalize(clazz.getSimpleName()));
    }

    private static String getBeanName(Annotation annotation) {
        return switch (annotation) {
            case Service service -> service.name();
            case Repository repo -> repo.name();
            default -> null;
        };
    }
}
