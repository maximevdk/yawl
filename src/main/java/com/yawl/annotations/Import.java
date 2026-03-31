package com.yawl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies additional {@link Configuration} classes to import when processing the annotated configuration.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Import {
    /**
     * The configuration classes to import.
     *
     * @return the imported classes
     */
    Class<?>[] value();
}
