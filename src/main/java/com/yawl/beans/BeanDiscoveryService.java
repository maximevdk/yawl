package com.yawl.beans;

import com.yawl.annotations.Bean;
import com.yawl.annotations.Configuration;
import com.yawl.annotations.Discoverable;
import com.yawl.annotations.HttpClient;
import com.yawl.annotations.Import;
import com.yawl.beans.model.BeanDefinition;
import com.yawl.common.util.BeanUtil;
import com.yawl.common.util.StringUtils;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.MethodInfo;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.function.Predicate.not;

/**
 * Scans the classpath to discover bean definitions from {@link Configuration} classes,
 * {@link Discoverable}-annotated components, and {@link HttpClient}-annotated interfaces.
 */
public class BeanDiscoveryService {

    /**
     * Discovers bean definitions declared in the given configuration class and its imports.
     *
     * @param configClass the configuration class to scan
     * @return the set of discovered bean definitions
     */
    public Set<BeanDefinition> discoverFromConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(Configuration.class)) {
            throw new IllegalArgumentException("No config class provided");
        }

        var classes = new ArrayList<Class<?>>();
        classes.add(configClass);

        if (configClass.isAnnotationPresent(Import.class)) {
            classes.addAll(Arrays.asList(configClass.getAnnotation(Import.class).value()));
        }

        var methods = classes.stream()
                .map(Class::getDeclaredMethods)
                .flatMap(Arrays::stream)
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .map(this::map);

        var configurations = classes.stream()
                .map(clazz -> new BeanDefinition(BeanUtil.getBeanName(clazz), clazz));

        return Stream.concat(methods, configurations).collect(Collectors.toSet());
    }

    /**
     * Scans the package of the given base class for all {@link Discoverable}-annotated components.
     *
     * @param pkg the package to be used as the scan root
     * @return the set of discovered bean definitions
     */
    public Set<BeanDefinition> discoverAll(Package pkg) {
        try (var result = new ClassGraph().enableAllInfo().acceptPackages(pkg.getName()).scan()) {
            return result.getClassesWithAnnotation(Discoverable.class)
                    .stream()
                    .filter(not(ClassInfo::isAnnotation))
                    .flatMap(this::map)
                    .collect(Collectors.toSet());
        }
    }

    /**
     * Discovers bean definitions from an explicit set of classes.
     *
     * @param classes the classes to inspect
     * @return the set of discovered bean definitions
     */
    public Set<BeanDefinition> discoverSet(Set<Class<?>> classes) {
        var classNames = classes.stream().map(Class::getName).toArray(String[]::new);
        try (var result = new ClassGraph().enableAllInfo().acceptClasses(classNames).scan()) {
            return result.getClassesWithAnnotation(Discoverable.class)
                    .stream()
                    .filter(not(ClassInfo::isAnnotation))
                    .flatMap(this::map)
                    .collect(Collectors.toSet());
        }
    }

    private Stream<BeanDefinition> map(ClassInfo info) {
        if (info.hasAnnotation(Configuration.class)) {
            var methods = info.getDeclaredMethodInfo().stream()
                    .filter(methodInfo -> methodInfo.hasAnnotation(Bean.class))
                    .map(MethodInfo::loadClassAndGetMethod)
                    .map(this::map);
            var configuration = info.getDeclaredConstructorInfo().stream()
                    .filter(MethodInfo::isConstructor)
                    .max(MethodInfo::compareTo)
                    .stream()
                    .map(this::map);
            return Stream.concat(methods, configuration);
        }

        if (info.hasAnnotation(HttpClient.class)) {
            var clazz = info.loadClass();
            return Stream.of(new BeanDefinition(BeanUtil.getBeanName(clazz), clazz));
        }

        return info.getDeclaredConstructorInfo().stream()
                .filter(MethodInfo::isConstructor)
                .max(MethodInfo::compareTo)
                .stream()
                .map(this::map);
    }

    private BeanDefinition map(Method method) {
        var bean = method.getAnnotation(Bean.class);
        var name = StringUtils.hasText(bean.name()) ? bean.name() : method.getName();
        var dependencies = Arrays.stream(method.getParameters()).map(this::map).toList();
        return new BeanDefinition(name, method.getReturnType(), dependencies, method);
    }

    private BeanDefinition map(MethodInfo method) {
        var constructor = method.loadClassAndGetConstructor();
        var clazz = constructor.getDeclaringClass();
        var dependencies = Arrays.stream(constructor.getParameters()).map(this::map).toList();
        return new BeanDefinition(BeanUtil.getBeanName(clazz), clazz, dependencies, null);
    }

    private BeanDefinition map(Parameter parameter) {
        var name = BeanUtil.getParameterName(parameter);
        return new BeanDefinition(name, parameter.getType());
    }
}
