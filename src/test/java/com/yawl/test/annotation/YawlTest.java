package com.yawl.test.annotation;

import com.yawl.test.extension.YawlTestExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ExtendWith(YawlTestExtension.class)
public @interface YawlTest {
    boolean dirtiesContext() default false;
}
