package com.yawl.util;

import com.yawl.exception.NoAccessibleConstructorFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public final class ConstructorUtil {
    private static final Logger log = LoggerFactory.getLogger(ConstructorUtil.class);

    public static <T> Optional<T> newInstance(Class<T> clazz, Object... args) {
        log.info("Finding suitable constructor for controller {}", (Object) clazz.getDeclaredConstructors());
        var constructor = Arrays.stream(clazz.getDeclaredConstructors())
                .filter(c -> c.canAccess(null))
                .filter(c -> c.getParameterCount() == args.length)
                .findFirst().orElseThrow(() -> NoAccessibleConstructorFoundException.forClass(clazz));

        try {
            return Optional.of((T) constructor.newInstance(args));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException ex) {
            log.error("Unable to create instance of class {}", clazz, ex);
            //ignore because it is our fault :grim:
            return Optional.empty();
        }
    }

    public static List<Class<?>> getRequiredConstructorParameters(Class<?> clazz) {
        var constructor = Arrays.stream(clazz.getDeclaredConstructors())
                .filter(c -> c.canAccess(null))
                .sorted((c1, c2) -> Integer.compare(c2.getParameterCount(), c1.getParameterCount())) // Reverse order
                .findFirst().orElseThrow(() -> NoAccessibleConstructorFoundException.forClass(clazz));


        log.info("Found constructor with parameters {}", constructor.getParameterTypes());
        return List.of(constructor.getParameterTypes());
    }

}
