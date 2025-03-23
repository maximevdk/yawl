package com.yawl.beans;

import com.yawl.exception.DuplicateBeanException;
import com.yawl.exception.NoSuchBeanException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class BeanRegistry {
    private static final Map<String, Object> BEANS = new ConcurrentHashMap<>();
    private static final Map<Class<?>, List<Object>> BEANS_BY_TYPE = new ConcurrentHashMap<>();

    public static <T> void registerBean(String name, T object) {
        if (object == null) {
            BEANS.remove(name);
            return;
        }

        if (BEANS.containsKey(name)) {
            throw DuplicateBeanException.forBeanName(name);
        }

        BEANS.put(name, object);
        BEANS_BY_TYPE.compute(object.getClass(), (key, value) -> {
            if (value == null) {
                return new ArrayList<>(List.of(object));
            } else {
                value.add(object);
                return value;
            }
        });
    }

    public static <T> T getBeanByName(String name, Class<T> clazz) {
        var bean = getBeanByName(name);

        if (clazz == bean.getClass()) {
            return (T) bean;
        }

        throw NoSuchBeanException.forClass(clazz);
    }

    public static <T> T getBeanByName(String name) {
        return (T) Optional.ofNullable(BEANS.get(name)).orElseThrow(() -> NoSuchBeanException.forName(name));
    }

    public static <T> T findBeanByType(Class<T> clazz) {
        var beansByType = BEANS_BY_TYPE.getOrDefault(clazz, List.of());

        if (beansByType.isEmpty()) {
            throw NoSuchBeanException.forClass(clazz);
        }

        if (beansByType.size() != 1) {
            throw DuplicateBeanException.forClass(clazz);
        }

        return (T) beansByType.getFirst();
    }

    public static Map<String, Class<?>> getBeans() {
        return BEANS.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().getClass()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}
