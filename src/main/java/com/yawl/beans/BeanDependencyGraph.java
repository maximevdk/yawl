package com.yawl.beans;

import com.yawl.beans.model.BeanDefinition;
import com.yawl.exception.DuplicateBeanException;
import com.yawl.exception.NoSuchBeanException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

class BeanDependencyGraph {
    private final Map<Class<?>, List<BeanDefinition>> beanDefinitionByClass = new HashMap<>();
    private final Map<String, BeanDefinition> beanDefinitionByName = new HashMap<>();

    BeanDependencyGraph(ApplicationContext ctx) {
        ctx.beansByName().entrySet()
                .stream()
                .map(entry -> new BeanDefinition(entry.getKey(), entry.getValue()))
                .forEach(this::addBeanToGraph);
    }

    void validate(Set<BeanDefinition> definitions) {
        for (BeanDefinition definition : definitions) {
            if (beanDefinitionByName.containsKey(definition.name())) {
                throw DuplicateBeanException.forBeanName(definition.name());
            }

            addBeanToGraph(definition);
        }

        for (BeanDefinition definition : definitions) {
            for (BeanDefinition dependency : definition.dependencies()) {
                if (!beanDefinitionByClass.containsKey(dependency.type())) {
                    throw NoSuchBeanException.forDependency(definition.type(), dependency.type());
                }
            }
        }
    }

    private void addBeanToGraph(BeanDefinition definition) {
        beanDefinitionByName.put(definition.name(), definition);

        //TODO: like app context is should be good to also add the interface or super class
        beanDefinitionByClass.compute(definition.type(), (_, value) -> {
            if (value == null) {
                value = new ArrayList<>();
            }

            value.add(definition);

            return value;
        });
    }


    BeanDefinition getDefinitionByNameAndOrType(String name, Class<?> type) {
        //TODO: consider if when the item isn't found by name it to be a configuration error
        if (beanDefinitionByName.containsKey(name)) {
            var definition = beanDefinitionByName.get(name);

            if (definition.type() == type) {
                return definition;
            }
        }

        return getDefinitionByType(type);
    }

    BeanDefinition getDefinitionByType(Class<?> clazz) {
        var definitions = beanDefinitionByClass.get(clazz);

        if (definitions == null || definitions.isEmpty()) {
            throw NoSuchBeanException.forClass(clazz);
        }

        if (definitions.size() > 1) {
            throw DuplicateBeanException.forClass(clazz);
        }

        return definitions.getFirst();
    }
}
