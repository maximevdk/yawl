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

public class BeanDiscoveryService {
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

    public Set<BeanDefinition> discoverAll(Class<?> baseClass) {
        try (var result = new ClassGraph().enableAllInfo().acceptPackages(baseClass.getPackageName()).scan()) {
            return result.getClassesWithAnnotation(Discoverable.class)
                    .stream()
                    .filter(not(ClassInfo::isAnnotation))
                    .flatMap(this::map)
                    .collect(Collectors.toSet());
        }
    }

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
            var httpClient = (HttpClient) info.getAnnotationInfo(HttpClient.class).loadClassAndInstantiate();
            return Stream.of(new BeanDefinition(httpClient.name(), info.loadClass()));
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
