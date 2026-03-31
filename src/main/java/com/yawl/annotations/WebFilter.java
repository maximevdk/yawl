package com.yawl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as a servlet filter bean, discoverable by the classpath scanner.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendedBy(jakarta.servlet.annotation.WebFilter.class)
@Discoverable
public @interface WebFilter {
    /**
     * The URL patterns this filter applies to.
     *
     * @return the URL patterns
     */
    String[] urlPatterns() default {"/*"};
}
