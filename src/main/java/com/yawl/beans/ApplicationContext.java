package com.yawl.beans;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

public class ApplicationContext {
    private final BeanRegistry registry = new BeanRegistry();

    public <T> T getBeanByNameOrThrow(String name) {
        return registry.getBeanByNameOrThrow(name);
    }

    public <T> T getBeanByNameOrThrow(String name, Class<T> clazz) {
        return registry.getBeanByNameOrThrow(name, clazz);
    }

    public <T> T getBeanByTypeOrThrow(Class<T> clazz) {
        return registry.findBeanByTypeOrThrow(clazz);
    }

    public <T> List<T> findBeansByType(Class<T> clazz) {
        return registry.findBeansByType(clazz);
    }

    public boolean containsBeanOfType(Class<?> clazz) {
        return registry.containsBeanOfType(clazz);
    }

    public void register(String name, Object bean) {
        register(name, bean, bean.getClass());
    }

    public void register(String name, Object bean, Class<?> clazz) {
        registry.registerBean(name, bean, clazz);
    }

    public List<Class<?>> getBeansAnnotatedWith(Class<? extends Annotation> annotation) {
        return registry.streamBeans()
                .<Class<?>>map(Object::getClass)
                .filter(clazz -> clazz.isAnnotationPresent(annotation))
                .toList();
    }

    public Map<String, Class<?>> beansByName() {
        return registry.getBeansByName();
    }

    public void clear() {
        registry.clear();
    }
}
