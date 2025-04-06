package com.yawl.util;

import com.yawl.annotations.Repository;
import com.yawl.annotations.Service;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BeanUtilTest {

    @Test
    void isBean() {
        assertTrue(BeanUtil.isBean(Test1.class), "Test1 is annotated with @Repository");
        assertTrue(BeanUtil.isBean(Test2.class), "Test2 is annotated with @Service");
        assertFalse(BeanUtil.isBean(Test3.class), "Test3 is not a bean");
    }

    @Repository
    class Test1 {
    }

    @Service
    class Test2 {
    }

    class Test3 {
    }
}