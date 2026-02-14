package com.yawl.beans;

import com.yawl.exception.DuplicateBeanException;
import com.yawl.exception.NoSuchBeanException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

final class BeanRegistry {
    private static final Map<String, Object> BEANS = new ConcurrentHashMap<>();
    private static final Map<Class<?>, List<Object>> BEANS_BY_TYPE = new ConcurrentHashMap<>();

    public static void registerBean(String name, Object object, Class<?> clazz) {
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

    public static <T> T getBeanByNameOrThrow(String name, Class<T> clazz) {
        var bean = getBeanByNameOrThrow(name);

        if (clazz == bean.getClass()) {
            return (T) bean;
        }

        throw NoSuchBeanException.forClass(clazz);
    }

    public static <T> T getBeanByNameOrThrow(String name) {
        return (T) Optional.ofNullable(BEANS.get(name)).orElseThrow(() -> NoSuchBeanException.forName(name));
    }

    public static <T> Optional<T> getBeanByName(String name) {
        return Optional.ofNullable((T) BEANS.get(name));
    }

    public static <T> T findBeanByTypeOrThrow(Class<T> clazz) {
        var beansByType = BEANS_BY_TYPE.getOrDefault(clazz, List.of());

        if (beansByType.isEmpty()) {
            throw NoSuchBeanException.forClass(clazz);
        }

        if (beansByType.size() != 1) {
            throw DuplicateBeanException.forClass(clazz);
        }

        return (T) beansByType.getFirst();
    }

    public static <T> boolean containsBeanOfType(Class<T> clazz) {
        return !BEANS_BY_TYPE.getOrDefault(clazz, List.of()).isEmpty();
    }

    public static <T> Optional<T> findBeanByType(Class<T> clazz) {
        var beansByType = BEANS_BY_TYPE.getOrDefault(clazz, List.of());

        if (beansByType.size() > 1) {
            throw DuplicateBeanException.forClass(clazz);
        }

        return beansByType.stream().map(item -> (T) item).findFirst();
    }

    public static Map<String, Class<?>> getBeansByName() {
        return BEANS.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().getClass()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public static Stream<Object> getBeans() {
        return BEANS.values().stream();
    }

    public static void clear() {
        BEANS.clear();
        BEANS_BY_TYPE.clear();
    }
}
