package com.yawl.http.model;

import javax.annotation.Nonnull;

public sealed interface PathSegment permits PathSegment.Capture, PathSegment.Literal {
    boolean matches(String path);

    /**
     * Represents a literal segment of the path.
     * Eg. in /users/{id} this would represent users
     *
     * @param path
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
