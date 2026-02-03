package com.yawl.beans;

import java.util.Map;

public class ApplicationContext {

    public <T> T getBeanByNameOrThrow(String name) {
        return BeanRegistry.getBeanByNameOrThrow(name);
    }

    public <T> T getBeanByNameOrThrow(String name, Class<T> clazz) {
        return BeanRegistry.getBeanByNameOrThrow(name, clazz);
    }

    public <T> T getBeanByTypeOrThrow(Class<T> clazz) {
        return BeanRegistry.findBeanByTypeOrThrow(clazz);
    }

    public boolean containsBeanOfType(Class<?> clazz) {
        return BeanRegistry.containsBeanOfType(clazz);
    }

    public void register(String name, Object bean) {
        BeanRegistry.registerBean(name, bean);
    }

    public void register(String name, Object bean, Class<?> clazz) {
        BeanRegistry.registerBean(name, bean, clazz);
    }

    public Map<String, Class<?>> beans() {
        return BeanRegistry.getBeans();
    }
}
