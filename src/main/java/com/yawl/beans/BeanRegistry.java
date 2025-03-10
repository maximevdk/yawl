package com.yawl.beans;

import com.yawl.exception.DuplicateBeanException;
import com.yawl.exception.NoSuchBeanException;

import java.util.HashMap;
import java.util.Map;

public class BeanRegistry {
    private static final Map<String, Object> BEANS = new HashMap<>();

    public static <T> void registerBean(String name, T object) {
        if (BEANS.containsKey(name)) {
            throw DuplicateBeanException.forBeanName(name);
        }

        BEANS.put(name, object);
    }

    public static <T> T getBeanByName(String name, Class<T> clazz) {
        var bean = getBeanByName(name);

        if (bean == null) {
            return null;
        }

        if (clazz == bean.getClass()) {
            return (T) bean;
        }

        return null;
    }

    public static <T> T getBeanByName(String name) {
        return (T) BEANS.get(name);
    }

    public static <T> T findBeanByType(Class<T> clazz) {
        return (T) BEANS.values().stream()
                .filter(obj -> obj.getClass() == clazz)
                .findFirst().orElseThrow(() -> NoSuchBeanException.forClass(clazz));
    }
}
