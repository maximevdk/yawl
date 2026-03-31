package com.yawl.beans;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Central container that holds all managed bean instances and provides lookup and registration operations.
 */
public class ApplicationContext {
    private final BeanRegistry registry = new BeanRegistry();

    /**
     * Returns the bean registered under the given name or throws if not found.
     *
     * @param name the bean name
     * @param <T>  the expected bean type
     * @return the bean instance
     */
    public <T> T getBeanByNameOrThrow(String name) {
        return registry.getBeanByNameOrThrow(name);
    }

    /**
     * Returns the bean registered under the given name cast to the specified type, or throws if not found.
     *
     * @param name  the bean name
     * @param clazz the expected bean type
     * @param <T>   the expected bean type
     * @return the bean instance
     */
    public <T> T getBeanByNameOrThrow(String name, Class<T> clazz) {
        return registry.getBeanByNameOrThrow(name, clazz);
    }

    /**
     * Returns the single bean matching the given type or throws if none or more than one is found.
     *
     * @param clazz the bean type
     * @param <T>   the expected bean type
     * @return the bean instance
     */
    public <T> T getBeanByTypeOrThrow(Class<T> clazz) {
        return registry.findBeanByTypeOrThrow(clazz);
    }

    /**
     * Returns all beans assignable to the given type.
     *
     * @param clazz the bean type
     * @param <T>   the expected bean type
     * @return a list of matching bean instances, possibly empty
     */
    public <T> List<T> findBeansByType(Class<T> clazz) {
        return registry.findBeansByType(clazz);
    }

    /**
     * Checks whether a bean of the given type is registered.
     *
     * @param clazz the bean type
     * @return {@code true} if at least one bean of the given type exists
     */
    public boolean containsBeanOfType(Class<?> clazz) {
        return registry.containsBeanOfType(clazz);
    }

    /**
     * Checks whether a bean with the given name is registered.
     *
     * @param name the bean name
     * @return {@code true} if a bean with the given name exists
     */
    public boolean containsBeanName(String name) {
        return registry.containsBeanName(name);
    }

    /**
     * Registers a bean under the given name, also registering it for all its interfaces and superclasses.
     *
     * @param name the bean name
     * @param bean the bean instance
     */
    public void register(String name, Object bean) {
        if (bean == null) {
            throw new IllegalArgumentException("Registered bean should not be null");
        }

        if (name == null) {
            throw new IllegalArgumentException("Registered bean name should not be null");
        }

        //register bean for its own class
        register(name, bean, bean.getClass());
        //also register this bean for all its interfaces and superclasses
        registerSuperClassAndInterfaces(bean, bean.getClass());
    }

    /**
     * Registers a bean under the given name for a specific type.
     *
     * @param name  the bean name
     * @param bean  the bean instance
     * @param clazz the type to register the bean as
     */
    public void register(String name, Object bean, Class<?> clazz) {
        if (bean == null) {
            throw new IllegalArgumentException("Registered bean should not be null");
        }

        if (name == null) {
            throw new IllegalArgumentException("Registered bean name should not be null");
        }

        if (clazz == null) {
            throw new IllegalArgumentException("Registered bean type should not be null");
        }

        registry.registerBean(name, bean, clazz);
    }

    /**
     * Returns the types of all beans annotated with the given annotation.
     *
     * @param annotation the annotation type to filter by
     * @return a list of bean types carrying the annotation
     */
    public List<Class<?>> getBeansAnnotatedWith(Class<? extends Annotation> annotation) {
        return registry.streamBeans()
                .<Class<?>>map(Map.Entry::getValue)
                .filter(clazz -> clazz.isAnnotationPresent(annotation))
                .toList();
    }

    /**
     * Returns a snapshot of all registered beans mapped by name to type.
     *
     * @return an unmodifiable map of bean name to bean type
     */
    public Map<String, Class<?>> beansByName() {
        return registry.streamBeans().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Removes all registered beans from the context.
     */
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

        if (atClass.getSuperclass() != null && (atClass.getSuperclass() != Object.class && atClass.getSuperclass() != Record.class)) {
            registry.registerBeanType(bean, atClass.getSuperclass());
            registerSuperClassAndInterfaces(bean, atClass.getSuperclass());
        }
    }
}
