package com.yawl.annotations;

import com.yawl.model.HttpStatus;
import com.yawl.model.MediaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface GetMapping {
    String path() default "/";

    String produces() default MediaType.APPLICATION_JSON_VALUE;

    HttpStatus status() default HttpStatus.OK;
}
