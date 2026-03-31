package com.yawl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an interface as a declarative HTTP client. A proxy implementation will be generated at runtime.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Discoverable
public @interface HttpClient {
    /**
     * The bean name for this HTTP client.
     *
     * @return the client bean name
     */
    String name();

    /**
     * The base path prepended to all request paths.
     *
     * @return the base path
     */
    String basePath() default "";

    /**
     * The base URL of the remote service.
     *
     * @return the base URL
     */
    String url();
}
