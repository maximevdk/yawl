package com.yawl.util;

import com.yawl.exception.NotInitializedException;
import com.yawl.model.InvocationResult;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

public final class ReflectionUtil {
    private static final Logger log = LoggerFactory.getLogger(ReflectionUtil.class);
    private static final MethodHandles.Lookup LOOKUP = MethodHandles.lookup();

    private static Reflections reflections = null;

    private ReflectionUtil() {
    }

    public static Set<Class<?>> getClassesAnnotatedWith(Class<? extends Annotation> annotationClass) {
        if (reflections == null) {
            throw new NotInitializedException("Call ReflectionUtil.init() before using this method");
        }

        return reflections.getTypesAnnotatedWith(annotationClass);
    }

    public static Set<Method> getMethodsAnnotatedWith(Class<? extends Annotation> annotationClass) {
        if (reflections == null) {
            throw new NotInitializedException("Call ReflectionUtil.init() before using this method");
        }

        return reflections.getMethodsAnnotatedWith(annotationClass);
    }

    public static InvocationResult invokeMethodOnInstance(Object instance, Method method, Object... args) {
        try {
            var result = method.invoke(instance, args);
            return InvocationResult.success(result);
        } catch (Exception ex) {
            log.error("Error invoking method {} on class {}", method.getName(), instance.getClass(), ex);
            return InvocationResult.failed(ex.getMessage());
        }
    }

    public static InvocationResult invokeMethod(MethodHandle method, List<?> arguments) {
        try {
            return InvocationResult.success(method.invokeWithArguments(arguments));
        } catch (Throwable ex) {
            log.error("Error invoking method {}. Is the MethodHandle not bound to an instance?", method.toString(), ex);
            return InvocationResult.failed(ex.getMessage());
        }
    }

    public static MethodHandle getBoundMethodHandle(Object instance, Method method) {
        try {
            var handle = LOOKUP.findVirtual(instance.getClass(), method.getName(), MethodType.methodType(method.getReturnType(), method.getParameterTypes()));
            return handle.bindTo(instance);
        } catch (Exception ex) {
            log.error("Unable to get MethodHandle for method {} on instance {}", method.getName(), instance.getClass());
            throw new RuntimeException("Unable to get MethodHandle", ex);
        }
    }

    public static void init(String basePackageName) {
        reflections = new Reflections(basePackageName);
    }
}
