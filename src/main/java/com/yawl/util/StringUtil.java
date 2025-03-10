package com.yawl.util;

public final class StringUtil {
    public static String decapitalize(String string) {
        char c[] = string.toCharArray();
        c[0] = Character.toLowerCase(c[0]);
        return new String(c);
    }
}
