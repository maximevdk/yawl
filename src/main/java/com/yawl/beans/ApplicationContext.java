package com.yawl.beans;

import java.lang.annotation.Annotation;
import java.util.List;
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
        register(name, bean, bean.getClass());
    }

    public void register(String name, Object bean, Class<?> clazz) {
        BeanRegistry.registerBean(name, bean, clazz);
    }

    public List<Class<?>> getBeansAnnotatedWith(Class<? extends Annotation> annotation) {
        return BeanRegistry.streamBeans()
                .<Class<?>>map(Object::getClass)
                .filter(clazz -> clazz.isAnnotationPresent(annotation))
                .toList();
    }

    public Map<String, Class<?>> beansByName() {
        return BeanRegistry.getBeansByName();
    }
}
