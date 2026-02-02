package com.yawl.beans;

public class ApplicationContext {

    public <T> T getBean(Class<T> clazz) {
        return BeanRegistry.findBeanByTypeOrThrow(clazz);
    }

    public <T> T getBeanByNameOrThrow(String name) {
        return BeanRegistry.getBeanByNameOrThrow(name);
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
}
