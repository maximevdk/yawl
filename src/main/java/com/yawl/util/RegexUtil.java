package com.yawl.util;

import javax.annotation.Nonnull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class RegexUtil {
    private static final Pattern PATH_PARAM_REGEX = Pattern.compile("\\{[^/]+\\}");
    private static final Pattern PATH_PARAM_REGEX_WITH_GROUP = Pattern.compile("\\{([^/]+)\\}");
    private static final Pattern COMMA_SEPARATED_LIST = Pattern.compile("\\s?+,?\\s+");

    /**
     * Transforms a path with a path parameter to a regex to match with other path e.g. /route/{id} → ^/route/([^/]+)$
     *
     * @param pathWithParam the path with a param eg. /route/{id}
     * @return the pattern with the path as a regex
     */
    public static Pattern pathParamsToRegex(@Nonnull String pathWithParam) {
        var regex = pathWithParam.replaceAll(PATH_PARAM_REGEX.pattern(), "([^/]+)");
        return Pattern.compile("^" + regex + "$");
    }

    public static Stream<String> enabledManagementEndpointsAsStream(@Nonnull String include) {
        return COMMA_SEPARATED_LIST.splitAsStream(include);
    }

    /**
     * Returns a matcher to find path params for a path
     *
     * @param path the path
     * @return a matcher that looks for path params
     */
    public static Matcher pathParam(@Nonnull String path) {
        return PATH_PARAM_REGEX_WITH_GROUP.matcher(path);
    }
}
