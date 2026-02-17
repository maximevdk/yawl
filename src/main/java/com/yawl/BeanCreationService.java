package com.yawl;

import com.yawl.annotations.Bean;
import com.yawl.annotations.Configuration;
import com.yawl.annotations.EnableHttpClients;
import com.yawl.annotations.HttpClient;
import com.yawl.annotations.TypedBean;
import com.yawl.beans.ApplicationContext;
import com.yawl.beans.CommonBeans;
import com.yawl.events.EventListenerRegistrar;
import com.yawl.exception.UnableToInitializeBeanException;
import com.yawl.http.client.ApacheHttpExecutor;
import com.yawl.http.client.HttpClientInvocationHandler;
import com.yawl.http.client.HttpExecutor;
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
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.collectingAndThen;

public class BeanCreationService {
    private static final Logger log = LoggerFactory.getLogger(BeanCreationService.class);
    private final Map<Class<?>, BeanWrapper<?>> wrapperCache = new HashMap<>();
    private final ApplicationContext applicationContext;
    private final EventListenerRegistrar eventRegistry;

    public BeanCreationService(ApplicationContext applicationContext, EventListenerRegistrar eventRegistry) {
        this.applicationContext = applicationContext;
        this.eventRegistry = eventRegistry;
    }

    public void findAndRegisterBeans() {
        ReflectionUtil.getClassesAnnotatedWith(EnableHttpClients.class).stream()
                .map(clazz -> clazz.getAnnotation(EnableHttpClients.class))
                .flatMap(enableHttpClients -> Arrays.stream(enableHttpClients.value()))
                .filter(client -> client.isAnnotationPresent(HttpClient.class))
                .collect(collectingAndThen(Collectors.toSet(), this::createHttpClients));

        //TODO: fix: even though more dynamic, Configuration annotated classes need to be defined first...
        var beans = new HashSet<BeanWrapper<?>>();
        ReflectionUtil.getClassesAnnotatedWith(Configuration.class).stream().flatMap(this::createWrappersWithSupplier).forEach(beans::add);
        ReflectionUtil.getClassesAnnotatedWith(TypedBean.class).stream().map(this::createWrapper).forEach(beans::add);
        beans.forEach(this::initializeBean);
    }

    public void findAndRegisterBeans(Set<Class<?>> includes) {
        includes.stream()
                .filter(include -> include.isAnnotationPresent(HttpClient.class))
                .collect(collectingAndThen(Collectors.toSet(), this::createHttpClients));

        //TODO: fix: this is a bit of duplicated code from findAndRegisterBeans() so should be cleaned up
        var beans = new HashSet<BeanWrapper<?>>();
        includes.stream().filter(include -> include.isAnnotationPresent(Configuration.class)).flatMap(this::createWrappersWithSupplier).forEach(beans::add);
        includes.stream().filter(include -> !include.isAnnotationPresent(Configuration.class)).map(this::createWrapper).forEach(beans::add);

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
                            return ReflectionUtil.invoke(method, configClassInstance, dependencyBeans)
                                    .orElseThrow(() -> UnableToInitializeBeanException.forClass(method.getReturnType()));
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
     * @param enabledHttpClients a set of classes annotated with {@code com.yawl.annotations.HttpClient}
     */
    private HttpExecutor createHttpClients(Set<Class<?>> enabledHttpClients) {
        var httpExecutor = new ApacheHttpExecutor(applicationContext.getBeanByNameOrThrow(CommonBeans.JSON_MAPPER_NAME));

        for (Class<?> httpClientClass : enabledHttpClients) {
            var httpClient = httpClientClass.getAnnotation(HttpClient.class);

            var proxy = Proxy.newProxyInstance(
                    httpClientClass.getClassLoader(),
                    new Class[]{httpClientClass},
                    new HttpClientInvocationHandler(httpExecutor)
            );

            applicationContext.register(httpClient.name(), proxy, httpClientClass);
        }

        return httpExecutor;
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
        eventRegistry.registerListeners(instance);
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
