package com.yawl.beans;

import com.yawl.beans.model.BeanDefinition;
import com.yawl.exception.DuplicateBeanException;
import com.yawl.exception.NoSuchBeanException;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BeanDependencyGraph {
    private final Map<Class<?>, List<BeanDefinition<?>>> beanDefinitionByClass = new HashMap<>();
    private final Map<String, BeanDefinition<?>> beanDefinitionByName = new HashMap<>();

    public void validate(Set<BeanDefinition<?>> definitions) {
        for (BeanDefinition<?> definition : definitions) {
            if (beanDefinitionByName.containsKey(definition.name())) {
                throw DuplicateBeanException.forBeanName(definition.name());
            }

            beanDefinitionByName.put(definition.name(), definition);

            beanDefinitionByClass.compute(definition.type(), (_, value) -> {
                if (value == null) {
                    value = new ArrayList<>();
                }

                value.add(definition);

                return value;
            });
        }

        for (BeanDefinition<?> definition : definitions) {
            for (Parameter dependency : definition.dependencies()) {
                if (!beanDefinitionByClass.containsKey(dependency.getType())) {
                    throw NoSuchBeanException.forClass(dependency.getType());
                }
            }
        }
    }

    public BeanDefinition<?> getDefinitionByName(String name) {
        return beanDefinitionByName.get(name);
    }

    public <T> BeanDefinition<T> getDefinitionByType(Class<T> clazz) {
        var definitions = beanDefinitionByClass.get(clazz);

        if (definitions == null || definitions.isEmpty()) {
            throw NoSuchBeanException.forClass(clazz);
        }

        if (definitions.size() > 1) {
            throw DuplicateBeanException.forClass(clazz);
        }

        return (BeanDefinition<T>) definitions.getFirst();
    }
}
