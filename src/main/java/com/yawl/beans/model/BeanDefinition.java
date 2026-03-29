package com.yawl.beans.model;

import jakarta.annotation.Nonnull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

public record BeanDefinition<T>(@Nonnull String name, @Nonnull Class<T> type, @Nonnull List<Parameter> dependencies,
                                Method beanCreationMethod) {
}
