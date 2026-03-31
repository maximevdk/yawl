package com.yawl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a service bean, discoverable by the classpath scanner.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Discoverable
public @interface Service {
    /**
     * The bean name. Defaults to the decapitalized class name if empty.
     *
     * @return the bean name
     */
    String name() default "";
}
