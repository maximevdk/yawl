package com.yawl.common.util;

import com.yawl.exception.NoAccessibleConstructorFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class ReflectionUtil {
    private static final Logger log = LoggerFactory.getLogger(ReflectionUtil.class);

    private ReflectionUtil() {
    }

    public static <T> Optional<T> invoke(Method method, Object instance, List<?> arguments) {
        try {
            return Optional.ofNullable((T) method.invoke(instance, arguments.toArray()));
        } catch (Exception ex) {
            //TODO: right now errors might get lost behind this since we return an empty optional, investigate if we should handle this differently
            log.error("Error invoking method {} on class {}", method.getName(), instance.getClass(), ex);
            return Optional.empty();
        }
    }

    public static <T> Optional<T> newInstance(Class<T> clazz, Object... args) {
        log.trace("Finding suitable constructor for class {}", (Object) clazz.getConstructors());
        var argumentTypes = Arrays.stream(args).map(Object::getClass).toArray(Class[]::new);
        var constructor = Arrays.stream(clazz.getDeclaredConstructors())
                .filter(c -> c.canAccess(null))
                .filter(c -> c.getParameterCount() == args.length)
                //TODO: also validate argument types.filter(c -> Arrays.deepEquals(c.getParameterTypes(), argumentTypes))
                .findFirst().orElseThrow(() -> NoAccessibleConstructorFoundException.of(clazz, args));
        try {
            return Optional.of((T) constructor.newInstance(args));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            log.error("Unable to create instance of class {} with args {}", clazz, args, ex);
            //ignore because it is our fault :grim:
            return Optional.empty();
        }
    }
}
