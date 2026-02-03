package com.yawl;

import com.yawl.annotations.Bean;
import com.yawl.annotations.Configuration;
import com.yawl.annotations.EnableHttpClients;
import com.yawl.annotations.HttpClient;
import com.yawl.annotations.Repository;
import com.yawl.annotations.Service;
import com.yawl.annotations.WebController;
import com.yawl.beans.ApplicationContext;
import com.yawl.beans.CommonBeans;
import com.yawl.exception.UnableToInitializeBeanException;
import com.yawl.http.ApacheHttpExecutor;
import com.yawl.http.HttpClientInvocationHandler;
import com.yawl.util.BeanUtil;
import com.yawl.util.ConstructorUtil;
import com.yawl.util.ReflectionUtil;
import com.yawl.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class BeanCreationService {
    private static final Logger log = LoggerFactory.getLogger(BeanCreationService.class);
    private final Map<Class<?>, BeanWrapper<?>> wrapperCache;
    private final ApplicationContext applicationContext;

    public BeanCreationService(ApplicationContext applicationContext) {
        this.wrapperCache = new HashMap<>();
        this.applicationContext = applicationContext;
    }

    public void findAndRegisterBeans() {
        ReflectionUtil.getClassAnnotatedWith(EnableHttpClients.class).ifPresent(this::createHttpClients);

        //TODO: fix: even though more dynamic, Configuration annotated classes need to be defined first...
        var beans = new HashSet<BeanWrapper<?>>();
        ReflectionUtil.getClassesAnnotatedWith(Configuration.class).stream().flatMap(this::createWrappersWithSupplier).forEach(beans::add);
        ReflectionUtil.getClassesAnnotatedWith(Service.class).stream().map(this::createWrapper).forEach(beans::add);
        ReflectionUtil.getClassesAnnotatedWith(Repository.class).stream().map(this::createWrapper).forEach(beans::add);
        ReflectionUtil.getClassesAnnotatedWith(WebController.class).stream().map(this::createWrapper).forEach(beans::add);

        beans.forEach(this::initializeBean);
    }

    private BeanWrapper<?> createWrapper(Class<?> clazz) {
        if (wrapperCache.containsKey(clazz)) {
            return wrapperCache.get(clazz);
        }

        if (applicationContext.containsBeanOfType(clazz)) {
            //if the dependency already exists in the registry, just return a placeholder wrapper
            return BeanWrapper.of(clazz);
        }

        var dependencies = ConstructorUtil.getRequiredConstructorParameters(clazz).stream()
                .map(Parameter::getType)
                .map(this::createWrapper)
                .toList();

        var beanName = BeanUtil.getBeanName(clazz);
        var wrapper = new BeanWrapper(beanName, clazz, dependencies, null);
        wrapperCache.put(clazz, wrapper);
        return wrapper;
    }

    private Stream<BeanWrapper<?>> createWrappersWithSupplier(Class<?> clazz) {
        //1. initialize configuration class
        var configClassInstance = ConstructorUtil.newInstance(clazz).orElseThrow();
        //2. return supplier for each method annotated with @Bean
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Bean.class))
                .map(method -> {
                    var parameters = List.of(method.getParameters());
                    var supplier = new Supplier<>() {
                        @Override
                        public Object get() {
                            log.debug("Creating bean of type type {}.", method.getReturnType());
                            var dependencyBeans = parameters.stream().map(parameter -> applicationContext.getBeanByTypeOrThrow(parameter.getType())).toList();
                            var invocation = ReflectionUtil.invokeMethodOnInstance(configClassInstance, method, dependencyBeans);

                            if (invocation.success() && invocation.resultAsOptional().isPresent()) {
                                return invocation.result();
                            }

                            throw UnableToInitializeBeanException.forClass(method.getReturnType());
                        }
                    };
                    var beanName = Optional.ofNullable(method.getAnnotation(Bean.class)).map(Bean::name).filter(StringUtils::hasText).orElse(method.getName());
                    var dependencies = parameters.stream().map(Parameter::getType).map(this::createWrapper).toList();
                    var wrapper = new BeanWrapper(beanName, method.getReturnType(), dependencies, supplier);
                    wrapperCache.put(method.getReturnType(), wrapper);
                    return wrapper;
                });
    }


    /**
     * Creates a proxy class for each HttpClient configured by {@code com.yawl.annotations.EnableHttpClients}
     *
     * @param enableHttpConfigClass a class annotated with {@code com.yawl.annotations.EnableHttpClients}
     */
    private void createHttpClients(Class<?> enableHttpConfigClass) {
        var httpClients = enableHttpConfigClass.getAnnotation(EnableHttpClients.class);
        var httpExecutor = new ApacheHttpExecutor(applicationContext.getBeanByNameOrThrow(CommonBeans.JSON_MAPPER_NAME));

        for (Class<?> httpClientClass : httpClients.classes()) {
            var httpClient = httpClientClass.getAnnotation(HttpClient.class);

            var proxy = Proxy.newProxyInstance(
                    httpClientClass.getClassLoader(),
                    new Class[]{httpClientClass},
                    new HttpClientInvocationHandler(httpExecutor)
            );

            applicationContext.register(httpClient.name(), proxy, httpClientClass);
        }
    }

    private void initializeBean(BeanWrapper<?> bean) {
        if (applicationContext.containsBeanOfType(bean.type())) {
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
                .map(applicationContext::getBeanByTypeOrThrow)
                .toArray();

        if (bean.dependencyCount() != dependencyBeans.length) {
            throw new IllegalArgumentException("Not all dependencies found for class %s. Expected %s but found %s".formatted(bean.type(), bean.dependencies(), dependencyBeans));
        }

        var instance = ConstructorUtil.newInstance(bean.type(), dependencyBeans).orElseThrow();
        registerBean(bean, instance);
    }

    private void registerBean(BeanWrapper<?> bean, Object instance) {
        applicationContext.register(bean.name(), instance, bean.type());
    }

    record BeanWrapper<T>(String name, Class<T> type, List<BeanWrapper<?>> dependencies, Supplier<T> supplier) {
        public int dependencyCount() {
            return dependencies.size();
        }

        public static <T> BeanWrapper<T> of(Class<T> type) {
            return new BeanWrapper<>(null, type, List.of(), null);
        }
    }
}
