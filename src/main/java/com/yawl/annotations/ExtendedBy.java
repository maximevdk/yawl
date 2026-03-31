package com.yawl.annotations;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meta-annotation that declares which annotations extend the annotated annotation type.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
//TODO: nothing is currently checking the extended by. re-add support for this in the future
public @interface ExtendedBy {
    /**
     * The annotation types that extend this annotation.
     *
     * @return the extending annotation types
     */
    Class<? extends Annotation>[] value() default {};
}
