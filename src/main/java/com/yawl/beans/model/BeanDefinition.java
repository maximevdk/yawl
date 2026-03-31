package com.yawl.beans.model;

import jakarta.annotation.Nonnull;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Describes a bean to be managed by the container, including its name, type, dependencies and optional factory method.
 *
 * @param name               the bean name
 * @param type               the bean type
 * @param dependencies       the bean's constructor or factory method dependencies
 * @param beanCreationMethod the factory method that produces this bean, or {@code null} for constructor-based beans
 */
public record BeanDefinition(@Nonnull String name, @Nonnull Class<?> type,
                             @Nonnull List<BeanDefinition> dependencies, Method beanCreationMethod) {

    /**
     * Convenience constructor for a bean with no dependencies and no factory method.
     *
     * @param name the bean name
     * @param type the bean type
     */
    public BeanDefinition(String name, Class<?> type) {
        this(name, type, List.of(), null);
    }
}
