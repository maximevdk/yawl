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

public class BeanService {
    private final ApplicationContext ctx;
    private final EventListenerRegistrar eventRegistry;
    private final BeanDependencyGraph graph;
    private final BeanDiscoveryService beanDiscoveryService;

    public BeanService(ApplicationContext ctx, EventListenerRegistrar eventRegistry) {
        this.ctx = ctx;
        this.eventRegistry = eventRegistry;
        this.graph = new BeanDependencyGraph();
        this.beanDiscoveryService = new BeanDiscoveryService();
    }

    public void loadAndInitializeConfig(Class<?> configClass) {
        var definitions = beanDiscoveryService.discoverFromConfigClass(configClass);
        graph.validate(definitions);
        definitions.forEach(this::lookupOrInitiate);
    }

    public void loadAndInitializeBeans(Class<?> baseClass) {
        var definitions = beanDiscoveryService.discoverAll(baseClass);
        graph.validate(definitions);
        definitions.forEach(this::lookupOrInitiate);
    }

    public void loadAndInitializeBeans(Set<Class<?>> classes) {
        var definitions = beanDiscoveryService.discoverSet(classes);
        graph.validate(definitions);
        definitions.forEach(this::lookupOrInitiate);
    }

    private <T> T lookupOrInitiate(BeanDefinition<T> definition) {
        if (ctx.containsBeanName(definition.name())) {
            return ctx.getBeanByNameOrThrow(definition.name(), definition.type());
        }

        var dependencies = definition.dependencies().stream()
                .map(parameter -> graph.getDefinitionByType(parameter.getType()))
                .map(this::lookupOrInitiate)
                .toList();

        if (definition.beanCreationMethod() != null) {
            var instance = ReflectionUtil.newInstance(definition.beanCreationMethod().getDeclaringClass()).orElseThrow();
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
                ctx.register(definition.name(), bean);
                eventRegistry.registerListeners(bean);
                return bean;
            }

            throw new IllegalArgumentException("Can't create bean from interface %s".formatted(definition.type()));
        }

        var bean = ReflectionUtil.newInstance(definition.type(), dependencies.toArray()).orElseThrow();
        ctx.register(definition.name(), bean);
        eventRegistry.registerListeners(bean);
        return bean;
    }
}
