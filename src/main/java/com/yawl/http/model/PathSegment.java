package com.yawl.http.model;


import jakarta.annotation.Nonnull;

/**
 * Represents a single segment of a URL path pattern, either a literal value or a capture variable.
 */
public sealed interface PathSegment permits PathSegment.Capture, PathSegment.Literal {
    /**
     * Returns whether this segment matches the given path segment string.
     *
     * @param path the path segment to match against
     * @return {@code true} if this segment matches
     */
    boolean matches(String path);

    /**
     * Represents a literal segment of the path.
     * Eg. in /users/{id} this would represent users
     *
     * @param path the literal path segment value
     */
    record Literal(@Nonnull String path) implements PathSegment {
        @Override
        public boolean matches(String path) {
            return this.path.equals(path);
        }
    }

    /**
     * Represents a capture segment of the path.
     * Eg. in /users/{id} this would represent {id}
     *
     * @param name the name of the capture path segment
     */
    record Capture(String name) implements PathSegment {
        @Override
        public boolean matches(String path) {
            return path != null && !path.isEmpty();
        }
    }
}
