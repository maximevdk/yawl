package com.yawl;

import com.yawl.annotations.*;
import com.yawl.beans.BeanRegistry;
import com.yawl.exception.UnableToInitializeBeanException;
import com.yawl.util.ConstructorUtil;
import com.yawl.util.ReflectionUtil;
import com.yawl.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.yawl.util.StringUtils.decapitalize;

public class BeanCreationService {
    private static final Logger log = LoggerFactory.getLogger(BeanCreationService.class);
    private final Map<Class<?>, BeanWrapper<?>> wrapperCache = new HashMap<>();

    public void findAndRegisterBeans() {
        var beans = new HashSet<BeanWrapper<?>>();
        //TODO: fix: even though more dynamic, Configuration annotated classes need to be defined first...
        ReflectionUtil.getClassesAnnotatedWith(Configuration.class).stream().flatMap(this::createWrappersWithSupplier).forEach(beans::add);
        ReflectionUtil.getClassesAnnotatedWith(Service.class).stream().map(this::createWrapper).forEach(beans::add);
        ReflectionUtil.getClassesAnnotatedWith(Repository.class).stream().map(this::createWrapper).forEach(beans::add);

        beans.forEach(this::initializeBean);
    }

    private BeanWrapper<?> createWrapper(Class<?> clazz) {
        if (wrapperCache.containsKey(clazz)) {
            return wrapperCache.get(clazz);
        }

        var dependencies = ConstructorUtil.getRequiredConstructorParameters(clazz).stream()
                .map(this::createWrapper)
                .toList();

        var wrapper = new BeanWrapper(clazz, dependencies, null);
        wrapperCache.put(clazz, wrapper);
        return wrapper;
    }

    private Stream<BeanWrapper<?>> createWrappersWithSupplier(Class<?> clazz) {
        //1. initialize configuration class
        var configClassInstance = ConstructorUtil.newInstance(clazz).orElseThrow();
        //2. return supplier for each method annotated with @Bean
        return Arrays.stream(clazz.getMethods())
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .map(method -> {
                    var dependencies = Arrays.stream(method.getParameterTypes()).map(this::createWrapper).toList();
                    var supplier = new Supplier<>() {
                        @Override
                        public Object get() {
                            log.debug("Creating bean of type type {}.", method.getReturnType());
                            var dependencyBeans = dependencies.stream().map(wrapper -> BeanRegistry.findBeanByTypeOrThrow(wrapper.type())).toArray();
                            var invocation = ReflectionUtil.invokeMethodOnInstance(configClassInstance, method, dependencyBeans);

                            if (invocation.success() && invocation.resultAsOptional().isPresent()) {
                                return invocation.result();
                            }

                            throw UnableToInitializeBeanException.forClass(method.getReturnType());
                        }
                    };

                    var wrapper = new BeanWrapper(method.getReturnType(), dependencies, supplier);
                    wrapperCache.put(method.getReturnType(), wrapper);
                    return wrapper;
                });
    }


    private void initializeBean(BeanWrapper<?> bean) {
        var self = BeanRegistry.findBeanByType(bean.type());
        if (self.isPresent()) {
            return;
        }

        for (BeanWrapper<?> wrapper : bean.dependencies()) {
            initializeBean(wrapper);
        }

        if (bean.supplier() != null) {
            var instance = bean.supplier().get();
            registerBean(bean, instance);
        } else {
            initializeAndRegisterBean(bean);
        }
    }


    private void initializeAndRegisterBean(BeanWrapper<?> bean) {
        log.info("Creating bean for class {}", bean);
        var dependencyBeans = bean.dependencies().stream()
                .map(BeanWrapper::type)
                .map(BeanRegistry::findBeanByTypeOrThrow)
                .toArray();

        if (bean.dependencyCount() != dependencyBeans.length) {
            throw new IllegalArgumentException("Not all dependencies found for class %s. Expected %s but found %s".formatted(bean.type(), bean.dependencies(), dependencyBeans));
        }

        var instance = ConstructorUtil.newInstance(bean.type(), dependencyBeans).orElseThrow();
        registerBean(bean, instance);
    }

    private void registerBean(BeanWrapper<?> bean, Object instance) {
        BeanRegistry.registerBean(bean.name(), instance, bean.type());
    }


    record BeanWrapper<T>(Class<T> type, List<BeanWrapper<?>> dependencies, Supplier<T> supplier) {
        public int dependencyCount() {
            return dependencies.size();
        }

        private String name() {
            return Optional.ofNullable(type.getAnnotation(NamedBean.class))
                    .map(NamedBean::name)
                    .filter(StringUtils::hasText)
                    .orElseGet(() -> decapitalize(type.getSimpleName()));
        }
    }
}
