package com.yawl.model;

public record Route(HttpMethod method, String path) {
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

    @Override
    public String toString() {
        return "%s:%s".formatted(method.name(), path);
    }

}
