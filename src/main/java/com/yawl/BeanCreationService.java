package com.yawl;

import com.yawl.annotations.Repository;
import com.yawl.annotations.Service;
import com.yawl.beans.BeanRegistry;
import com.yawl.util.ConstructorUtil;
import com.yawl.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.yawl.util.StringUtils.decapitalize;

public class BeanCreationService {
    private static final Logger log = LoggerFactory.getLogger(BeanCreationService.class);
    private final ReflectionUtil reflectionUtil;

    public BeanCreationService(ReflectionUtil reflectionUtil) {
        this.reflectionUtil = reflectionUtil;
    }

    public void findAndRegisterBeans() {
        var beans = new HashSet<Class<?>>();

        beans.addAll(reflectionUtil.getClassesAnnotatedWith(Repository.class));
        beans.addAll(reflectionUtil.getClassesAnnotatedWith(Service.class));

        beans.stream()
                .map(clazz -> Map.entry(clazz, ConstructorUtil.getRequiredConstructorParameters(clazz)))
                .sorted(Comparator.comparingInt(entry -> entry.getValue().size())) // sort so that the entries with the least dependencies go first
                .forEach(entry -> registerBean(entry.getKey(), entry.getValue())); // which gives more chance of finding the other dependencies faster
    }

    private static void registerBean(Class<?> clazz, List<Class<?>> dependencies) {
        log.info("Creating bean for class {}", clazz);
        var dependencyBeans = dependencies.stream()
                .map(BeanRegistry::findBeanByType)
                .toArray();
        ConstructorUtil.newInstance(clazz, dependencyBeans)
                .ifPresent(instance -> BeanRegistry.registerBean(decapitalize(clazz.getSimpleName()), instance));
    }
}
