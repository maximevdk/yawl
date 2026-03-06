package com.yawl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendedBy(jakarta.servlet.annotation.WebFilter.class)
public @interface WebFilter {
    String[] urlPatterns() default {"/*"};
}
