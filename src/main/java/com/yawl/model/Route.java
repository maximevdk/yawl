package com.yawl.model;

import com.yawl.util.RegexUtil;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public record Route(@Nonnull HttpMethod method, @Nonnull String path) {
    public static Route of(HttpMethod method, String... paths) {
        var builder = new StringBuilder();

        for (String path : paths) {
            if (path == null || path.isEmpty()) {
                continue;
            }

            path = path.replaceFirst("^/", "");

            if (!path.isEmpty()) {
                builder.append("/").append(path);
            }
        }

        return new Route(method, builder.toString());
    }

    public boolean hasPathParam() {
        return RegexUtil.pathParam(path).find();
    }

    public List<String> pathParams() {
        var matcher = RegexUtil.pathParam(path);
        var pathParams = new ArrayList<String>();

        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                pathParams.add(matcher.group(i));
            }
        }

        return pathParams;
    }

    /**
     * Checks if the two routes are the same.
     * If the two paths are not a direct match, it will try to verify if there are path params which causes that
     * the routes don't match to each other 100%.
     *
     * @param other the reference object with which to compare.
     * @return true if the routes are identical or match each other
     */
    public boolean matches(Route other) {
        if (this.method == other.method) {
            if (this.path.equals(other.path)) {
                return true;
            }

            if (hasPathParam()) {
                return RegexUtil.pathParamsToRegex(path).matcher(other.path).matches();
            }

            if (other.hasPathParam()) {
                return RegexUtil.pathParamsToRegex(other.path).matcher(path).matches();
            }
        }

        return false;
    }


    @Override
    public String toString() {
        return "%s:%s".formatted(method.name(), path);
    }

}
