package com.yawl.annotations;

import com.yawl.http.model.ContentType;
import com.yawl.http.model.HttpStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Maps an HTTP DELETE request to the annotated handler method.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DeleteMapping {
    /**
     * The URL path pattern for this mapping.
     *
     * @return the path pattern
     */
    String path() default "/";

    /**
     * The content type produced by this endpoint.
     *
     * @return the produced content type
     */
    String produces() default ContentType.APPLICATION_JSON_VALUE;

    /**
     * The HTTP status code returned on success.
     *
     * @return the HTTP status
     */
    HttpStatus status() default HttpStatus.OK;
}
