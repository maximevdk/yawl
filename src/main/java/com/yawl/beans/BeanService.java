package com.yawl.beans;

import com.yawl.annotations.HttpClient;
import com.yawl.beans.model.BeanDefinition;
import com.yawl.common.util.ReflectionUtil;
import com.yawl.events.EventListenerRegistrar;
import com.yawl.exception.UnableToInitializeBeanException;
import com.yawl.http.client.HttpClientInvocationHandler;
import com.yawl.http.client.HttpExecutor;

import java.lang.reflect.Proxy;
import java.util.Set;

/**
 * Orchestrates bean discovery, dependency graph validation and bean instantiation into the {@link ApplicationContext}.
 */
public class BeanService {
    private final ApplicationContext ctx;
    private final EventListenerRegistrar eventRegistry;
    private final BeanDependencyGraph graph;
    private final BeanDiscoveryService beanDiscoveryService;

    public BeanService(ApplicationContext ctx) {
        this.ctx = ctx;
        this.eventRegistry = ctx.getBeanByTypeOrThrow(EventListenerRegistrar.class);
        this.graph = new BeanDependencyGraph(ctx);
        this.beanDiscoveryService = new BeanDiscoveryService();
    }

    /**
     * Discovers and initializes beans declared in the given configuration class.
     *
     * @param configClass the configuration class to process
     */
    public void loadAndInitializeConfig(Class<?> configClass) {
        var definitions = beanDiscoveryService.discoverFromConfigClass(configClass);
        graph.validate(definitions);
        definitions.forEach(this::lookupOrInitiate);
    }

    /**
     * Scans the package of the given base class and initializes all discoverable beans.
     *
     * @param baseClass the class whose package is used as the scan root
     */
    public void loadAndInitializeBeans(Class<?> baseClass) {
        var definitions = beanDiscoveryService.discoverAll(baseClass.getPackage());
        graph.validate(definitions);
        definitions.forEach(this::lookupOrInitiate);
    }

    /**
     * Discovers and initializes beans from an explicit set of classes.
     *
     * @param classes the classes to inspect and instantiate
     */
    public void loadAndInitializeBeans(Set<Class<?>> classes) {
        var definitions = beanDiscoveryService.discoverSet(classes);
        graph.validate(definitions);
        definitions.forEach(this::lookupOrInitiate);
    }

    private <T> T lookupOrInitiate(BeanDefinition definition) {
        if (ctx.containsBeanName(definition.name())) {
            return (T) ctx.getBeanByNameOrThrow(definition.name(), definition.type());
        }

        var dependencies = definition.dependencies().stream()
                .map(parameter -> graph.getDefinitionByNameAndOrType(parameter.name(), parameter.type()))
                .map(this::lookupOrInitiate)
                .toList();

        if (definition.beanCreationMethod() != null) {
            var instance = lookupOrInitiate(graph.getDefinitionByType(definition.beanCreationMethod().getDeclaringClass()));
            var bean = (T) ReflectionUtil.invoke(definition.beanCreationMethod(), instance, dependencies)
                    .orElseThrow(() -> UnableToInitializeBeanException.forClass(definition.beanCreationMethod().getReturnType()));
            ctx.register(definition.name(), bean);
            eventRegistry.registerListeners(bean);
            return bean;
        }

        if (definition.type().isInterface()) {
            if (definition.type().isAnnotationPresent(HttpClient.class)) {
                var bean = (T) Proxy.newProxyInstance(
                        definition.type().getClassLoader(),
                        new Class[]{definition.type()},
                        new HttpClientInvocationHandler(lookupOrInitiate(graph.getDefinitionByType(HttpExecutor.class)))
                );
                ctx.register(definition.name(), bean, definition.type());
                eventRegistry.registerListeners(bean);
                return bean;
            }

            throw new IllegalArgumentException("Can't create bean from interface %s".formatted(definition.type()));
        }

        var bean = ReflectionUtil.newInstance(definition.type(), dependencies.toArray()).orElseThrow();
        ctx.register(definition.name(), bean);
        eventRegistry.registerListeners(bean);
        return (T) bean;
    }
}
