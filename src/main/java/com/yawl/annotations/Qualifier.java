package com.yawl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the bean name to inject when multiple candidates of the same type exist.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Qualifier {
    /**
     * The specific bean name.
     *
     * @return the bean name
     */
    String value();
}
