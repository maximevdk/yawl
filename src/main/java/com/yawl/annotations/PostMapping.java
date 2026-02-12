package com.yawl.annotations;

import com.yawl.http.model.HttpStatus;
import com.yawl.http.model.ContentType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface PostMapping {
    String path() default "/";

    String produces() default ContentType.APPLICATION_JSON_VALUE;

    HttpStatus status() default HttpStatus.OK;
}
