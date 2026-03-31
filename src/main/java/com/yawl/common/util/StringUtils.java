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

/**
 * Utility methods for string manipulation, parsing, and type conversion.
 */
public final class StringUtils {
    private StringUtils() {}

    private static final Map<Type, Function<String, ?>> STRING_PARSER_FN = Map.of(
            int.class, Integer::parseInt,
            Integer.class, Integer::parseInt,
            Long.class, Long::parseLong,
            UUID.class, UUID::fromString,
            LocalDate.class, LocalDate::parse
    );

    /**
     * Returns whether the given string is non-null and contains non-whitespace characters.
     *
     * @param value the string to check
     * @return {@code true} if the string has text
     */
    public static boolean hasText(String value) {
        if (value == null) {
            return false;
        }

        return !value.trim().isEmpty();
    }

    /**
     * Converts the first character of the given string to lowercase.
     *
     * @param string the string to decapitalize
     * @return the decapitalized string
     */
    public static String decapitalize(String string) {
        if (string == null || string.isEmpty()) {
            throw new IllegalArgumentException("Null or empty string provided");
        }

        char c[] = string.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        return new String(c);
    }


    /**
     * Parses an array of string values into the given target type, supporting collections.
     *
     * @param values the string values to parse
     * @param type   the target type
     * @param <T>    the result type
     * @return the parsed value, or {@code null} if the input is null or empty
     */
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

        if (values == null || values.length == 0) {
            return null;
        }

        return parse(String.join(",", values), type);
    }


    /**
     * Parses a single string value into the given target type.
     *
     * @param value the string to parse
     * @param clazz the target type
     * @param <T>   the result type
     * @return the parsed value, or {@code null} if the input is null
     */
    public static <T> T parse(String value, Type clazz) {
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

    /**
     * Converts the given object to its string representation.
     *
     * @param object the object to convert
     * @return the string representation, or {@code null} if the input is null
     */
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
