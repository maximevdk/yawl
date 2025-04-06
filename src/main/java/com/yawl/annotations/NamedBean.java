package com.yawl.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface NamedBean {
    /**
     * The beans name, default the class name
     *
     * @return the name of the bean
     */
    String name() default "";
}
