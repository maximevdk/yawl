package com.yawl;

import com.yawl.annotations.Bean;
import com.yawl.annotations.Configuration;
import com.yawl.annotations.EnableHttpClients;
import com.yawl.annotations.HttpClient;
import com.yawl.annotations.NamedBean;
import com.yawl.annotations.Repository;
import com.yawl.annotations.Service;
import com.yawl.beans.BeanRegistry;
import com.yawl.beans.CommonBeans;
import com.yawl.exception.UnableToInitializeBeanException;
import com.yawl.http.ApacheHttpExecutor;
import com.yawl.http.HttpClientInvocationHandler;
import com.yawl.util.ConstructorUtil;
import com.yawl.util.ReflectionUtil;
import com.yawl.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static com.yawl.util.StringUtils.decapitalize;

public class BeanCreationService {
    private static final Logger log = LoggerFactory.getLogger(BeanCreationService.class);
    private final Map<Class<?>, BeanWrapper<?>> wrapperCache = new HashMap<>();

    public void findAndRegisterBeans() {
        ReflectionUtil.getClassAnnotatedWith(EnableHttpClients.class).ifPresent(this::createHttpClients);

        //TODO: fix: even though more dynamic, Configuration annotated classes need to be defined first...
        var beans = new HashSet<BeanWrapper<?>>();
        ReflectionUtil.getClassesAnnotatedWith(Configuration.class).stream().flatMap(this::createWrappersWithSupplier).forEach(beans::add);
        ReflectionUtil.getClassesAnnotatedWith(Service.class).stream().map(this::createWrapper).forEach(beans::add);
        ReflectionUtil.getClassesAnnotatedWith(Repository.class).stream().map(this::createWrapper).forEach(beans::add);

        beans.forEach(this::initializeBean);
    }

    private BeanWrapper<?> createWrapper(Class<?> clazz) {
        if (wrapperCache.containsKey(clazz)) {
            return wrapperCache.get(clazz);
        }

        if (BeanRegistry.containsBeanOfType(clazz)) {
            //if the dependency already exists in the registry, just return a placeholder wrapper
            return BeanWrapper.of(clazz);
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


    /**
     * Creates a proxy class for each HttpClient configured by {@code com.yawl.annotations.EnableHttpClients}
     * @param enableHttpConfigClass a class annotated with {@code com.yawl.annotations.EnableHttpClients}
     */
    private void createHttpClients(Class<?> enableHttpConfigClass) {
        var httpClients = enableHttpConfigClass.getAnnotation(EnableHttpClients.class);
        var httpExecutor = new ApacheHttpExecutor(BeanRegistry.getBeanByNameOrThrow(CommonBeans.JSON_MAPPER_NAME));

        for (Class<?> httpClientClass : httpClients.classes()) {
            var httpClient = httpClientClass.getAnnotation(HttpClient.class);

            var proxy = Proxy.newProxyInstance(
                    httpClientClass.getClassLoader(),
                    new Class[]{httpClientClass},
                    new HttpClientInvocationHandler(httpExecutor)
            );

            BeanRegistry.registerBean(httpClient.name(), proxy, httpClientClass);
        }
    }

    private void initializeBean(BeanWrapper<?> bean) {
        if (BeanRegistry.containsBeanOfType(bean.type())) {
            //do not re-initialize beans that are already in the bean registry
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

        public static <T> BeanWrapper<T> of(Class<T> type) {
            return new BeanWrapper<T>(type, List.of(), null);
        }
    }
}
