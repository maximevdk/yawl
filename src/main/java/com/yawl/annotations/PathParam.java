package com.yawl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Binds a method parameter to a path variable extracted from the request URI.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface PathParam {
    // todo: required for now until I figure out how to include the parameter name in the compiled class (debug information)
    // see https://stackoverflow.com/questions/6759880/getting-the-name-of-a-method-parameter/6759953#6759953
    /**
     * The name of the path variable.
     *
     * @return the path variable name
     */
    String name();
}
