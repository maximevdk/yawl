package com.yawl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method in a {@link Configuration} class as a bean producer. The return value is registered in the application context.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Bean {
    /**
     * The name under which the bean is registered. Defaults to the method name if empty.
     *
     * @return the bean name
     */
    String name() default "";
}
