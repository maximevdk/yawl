package com.yawl.beans;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        if (bean == null) {
            throw new NullPointerException("Registered bean should not be null");
        }

        if (name == null) {
            throw new NullPointerException("Registered bean name should not be null");
        }

        //register bean for its own class
        register(name, bean, bean.getClass());
        //also register this bean for all its interfaces and superclasses
        registerSuperClassAndInterfaces(bean, bean.getClass());
    }

    public void register(String name, Object bean, Class<?> clazz) {
        if (bean == null) {
            throw new NullPointerException("Registered bean should not be null");
        }

        if (name == null) {
            throw new NullPointerException("Registered bean name should not be null");
        }

        if (clazz == null) {
            throw new NullPointerException("Registered bean type should not be null");
        }

        registry.registerBean(name, bean, clazz);
    }

    public List<Class<?>> getBeansAnnotatedWith(Class<? extends Annotation> annotation) {
        return registry.streamBeans()
                .<Class<?>>map(Map.Entry::getValue)
                .filter(clazz -> clazz.isAnnotationPresent(annotation))
                .toList();
    }

    public Map<String, Class<?>> beansByName() {
        return registry.streamBeans().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void clear() {
        registry.clear();
    }


    private void registerSuperClassAndInterfaces(Object bean, Class<?> atClass) {
        if (atClass == null) {
            return;
        }

        for (Class<?> clazz : atClass.getInterfaces()) {
            registry.registerBeanType(bean, clazz);
            registerSuperClassAndInterfaces(bean, clazz);
        }

        if (atClass.getSuperclass() != null && atClass.getSuperclass() != Object.class) {
            registry.registerBeanType(bean, atClass.getSuperclass());
            registerSuperClassAndInterfaces(bean, atClass.getSuperclass());
        }
    }
}
