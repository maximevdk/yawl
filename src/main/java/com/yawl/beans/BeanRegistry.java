package com.yawl.beans;

import com.yawl.exception.DuplicateBeanException;
import com.yawl.exception.NoSuchBeanException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

final class BeanRegistry {
    private final Map<String, Object> BEANS = new ConcurrentHashMap<>();
    private final Map<Class<?>, List<Object>> BEANS_BY_TYPE = new ConcurrentHashMap<>();

    void registerBean(String name, Object object, Class<?> clazz) {
        if (BEANS.containsKey(name)) {
            throw DuplicateBeanException.forBeanName(name);
        }

        BEANS.put(name, object);
        BEANS_BY_TYPE.compute(clazz, (key, value) -> {
            if (value == null) {
                return new ArrayList<>(List.of(object));
            } else {
                value.add(object);
                return value;
            }
        });
    }

    /**
     * Register instance just by type, the instance won't be findable by name.
     * This method should only be used to register inheriting interfaces or superclasses.
     *
     * @param object the instance
     * @param clazz  the interface/superclass type of the instance
     */
    void registerBeanType(Object object, Class<?> clazz) {
        BEANS_BY_TYPE.compute(clazz, (key, value) -> {
            if (value == null) {
                return new ArrayList<>(List.of(object));
            } else {
                var shouldBeAdded = value.stream()
                        .noneMatch(obj -> Objects.equals(obj, object));

                if (shouldBeAdded) {
                    value.add(object);
                }
                return value;
            }
        });
    }

    <T> T getBeanByNameOrThrow(String name, Class<T> clazz) {
        var bean = getBeanByNameOrThrow(name);

        if (clazz.isAssignableFrom(bean.getClass())) {
            return (T) bean;
        }

        throw NoSuchBeanException.forClass(clazz);
    }

    <T> T getBeanByNameOrThrow(String name) {
        return (T) Optional.ofNullable(BEANS.get(name)).orElseThrow(() -> NoSuchBeanException.forName(name));
    }

    <T> Optional<T> getBeanByName(String name) {
        return Optional.ofNullable((T) BEANS.get(name));
    }

    <T> T findBeanByTypeOrThrow(Class<T> clazz) {
        var beansByType = BEANS_BY_TYPE.getOrDefault(clazz, List.of());

        if (beansByType.isEmpty()) {
            throw NoSuchBeanException.forClass(clazz);
        }

        if (beansByType.size() != 1) {
            throw DuplicateBeanException.forClass(clazz);
        }

        return (T) beansByType.getFirst();
    }

    <T> List<T> findBeansByType(Class<T> clazz) {
        return (List<T>) BEANS_BY_TYPE.getOrDefault(clazz, List.of());
    }

    <T> boolean containsBeanOfType(Class<T> clazz) {
        return !BEANS_BY_TYPE.getOrDefault(clazz, List.of()).isEmpty();
    }

    <T> Optional<T> findBeanByType(Class<T> clazz) {
        var beansByType = BEANS_BY_TYPE.getOrDefault(clazz, List.of());

        if (beansByType.size() > 1) {
            throw DuplicateBeanException.forClass(clazz);
        }

        return beansByType.stream().map(item -> (T) item).findFirst();
    }

    Stream<Map.Entry<String, Class<?>>> streamBeans() {
        return BEANS.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().getClass()));
    }

    void clear() {
        BEANS.clear();
        BEANS_BY_TYPE.clear();
    }
}
