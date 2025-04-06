package com.yawl.util;

import java.util.Map;
import java.util.function.Function;

public final class StringUtils {
    private static final Map<Class<?>, Function<String, ?>> STRING_PARSER_FN = Map.of(
            int.class, Integer::parseInt,
            Integer.class, Integer::parseInt
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


    public static <T> T parse(String value, Class<T> clazz) {
        if (clazz == String.class) {
            return (T) value;
        }

        var fn = STRING_PARSER_FN.get(clazz);

        if (fn == null) {
            throw new IllegalArgumentException("No support to parse class as string " + clazz);
        }

        return (T) fn.apply(value);
    }
}
