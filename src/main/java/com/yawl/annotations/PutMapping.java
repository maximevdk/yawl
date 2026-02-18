package com.yawl.annotations;

import com.yawl.http.model.ContentType;
import com.yawl.http.model.HttpStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface PutMapping {
    String path() default "/";

    String produces() default ContentType.APPLICATION_JSON_VALUE;

    HttpStatus status() default HttpStatus.OK;
}
