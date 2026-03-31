package com.yawl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as an HTTP controller whose methods handle incoming web requests.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Discoverable
public @interface WebController {
    /**
     * The base path for all routes in this controller.
     *
     * @return the base path
     */
    String path() default "/";
}
