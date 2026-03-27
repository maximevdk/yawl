package com.yawl.common.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class StringUtils {
    private static final Map<Class<?>, Function<String, ?>> STRING_PARSER_FN = Map.of(
            int.class, Integer::parseInt,
            Integer.class, Integer::parseInt,
            Long.class, Long::parseLong,
            UUID.class, UUID::fromString,
            LocalDate.class, LocalDate::parse
    );

    public static boolean hasText(String value) {
        if (value == null) {
            return false;
        }

        return !value.trim().isEmpty();
    }

    public static String decapitalize(String string) {
        if (string == null || string.isEmpty()) {
            throw new IllegalArgumentException("Null or empty string provided");
        }

        char c[] = string.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        return new String(c);
    }


    public static <T> T parse(String[] values, Type type) {
        if (type instanceof ParameterizedType parameterizedType) {
            var elementType = (Class<T>) parameterizedType.getActualTypeArguments()[0];
            if (parameterizedType.getRawType() == List.class) {
                return (T) Arrays.stream(Optional.ofNullable(values).orElse(new String[0]))
                        .map(value -> value.split(","))
                        .flatMap(Arrays::stream)
                        .map(String::trim)
                        .filter(StringUtils::hasText)
                        .map(value -> parse(value, elementType))
                        .toList();
            } else if (parameterizedType.getRawType() == Set.class) {
                return (T) Arrays.stream(Optional.ofNullable(values).orElse(new String[0]))
                        .map(value -> value.split(","))
                        .flatMap(Arrays::stream)
                        .map(String::trim)
                        .filter(StringUtils::hasText)
                        .map(value -> parse(value, elementType))
                        .collect(Collectors.toUnmodifiableSet());
            }
        }

        if (values == null) {
            return null;
        }

        return parse(String.join(",", values), (Class<T>) type);
    }


    private static <T> T parse(String value, Class<T> clazz) {
        if (value == null) {
            return null;
        }

        if (clazz == String.class) {
            return (T) value;
        }

        var fn = STRING_PARSER_FN.get(clazz);

        if (fn == null) {
            throw new IllegalArgumentException("No support to parse class as string " + clazz);
        }

        return (T) fn.apply(value);
    }

    public static String toString(Object object) {
        if (object == null) {
            return null;
        }

        if (object instanceof String str) {
            return str;
        }

        if (object instanceof Collection<?> collection) {
            return collection.stream().map(StringUtils::toString).collect(Collectors.joining(","));
        }

        return object.toString();
    }
}
