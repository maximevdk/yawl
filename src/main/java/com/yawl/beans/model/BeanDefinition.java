package com.yawl.beans.model;

import jakarta.annotation.Nonnull;

import java.lang.reflect.Method;
import java.util.List;

public record BeanDefinition(@Nonnull String name, @Nonnull Class<?> type,
                             @Nonnull List<BeanDefinition> dependencies, Method beanCreationMethod) {

    public BeanDefinition(String name, Class<?> type) {
        this(name, type, List.of(), null);
    }
}
