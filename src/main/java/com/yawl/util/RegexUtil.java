package com.yawl.util;

import javax.annotation.Nonnull;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class RegexUtil {
    private static final Pattern COMMA_SEPARATED_LIST = Pattern.compile("\\s?+,?\\s+");

    public static Stream<String> enabledManagementEndpointsAsStream(@Nonnull String include) {
        return COMMA_SEPARATED_LIST.splitAsStream(include);
    }

}
