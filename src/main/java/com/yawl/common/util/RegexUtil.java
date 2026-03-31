package com.yawl.common.util;

import jakarta.annotation.Nonnull;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Utility methods for regex-based parsing of configuration values and command-line arguments.
 */
public class RegexUtil {
    private RegexUtil() {}

    private static final Pattern COMMAND_LINE_ARGUMENTS = Pattern.compile("^--(.+)=(.+)$");
    private static final Pattern COMMA_SEPARATED_LIST = Pattern.compile("\\s?+,?\\s+");

    /**
     * Splits the given comma-separated string into a stream of management endpoint names.
     *
     * @param include the comma-separated endpoint names
     * @return a stream of endpoint name strings
     */
    public static Stream<String> enabledManagementEndpointsAsStream(@Nonnull String include) {
        return COMMA_SEPARATED_LIST.splitAsStream(include);
    }

    /**
     * Parses command-line arguments of the form {@code --key=value} into a map.
     *
     * @param args the command-line arguments
     * @return a map of argument keys to values
     */
    public static Map<String, String> parseCommandLineArguments(String... args) {
        return Arrays.stream(args)
                .map(COMMAND_LINE_ARGUMENTS::matcher)
                .filter(Matcher::matches)
                .collect(Collectors.toMap(matcher -> matcher.group(1), matcher -> matcher.group(2)));
    }

}
