package com.yawl.util;

import com.yawl.annotations.Repository;
import com.yawl.annotations.Service;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

public final class BeanUtil {
    private static final List<Class<? extends Annotation>> ENABLED_ANNOTATIONS = List.of(Service.class, Repository.class);

    public static boolean isBean(Class<?> clazz) {
        return Arrays.stream(clazz.getAnnotations())
                .map(Annotation::annotationType)
                .anyMatch(ENABLED_ANNOTATIONS::contains);
    }
}
