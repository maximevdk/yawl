package com.yawl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Binds a method parameter to an HTTP request header value.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface RequestHeader {
    /**
     * The header name.
     *
     * @return the header name
     */
    String name();

    /**
     * Whether the header is required.
     *
     * @return {@code true} if required
     */
    boolean required() default true;
}
