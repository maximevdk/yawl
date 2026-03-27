package com.yawl.util;

import com.yawl.annotations.Repository;
import com.yawl.annotations.Service;
import com.yawl.annotations.WebController;
import com.yawl.common.util.BeanUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BeanUtilTest {

    @Test
    void isBean() {
        assertTrue(BeanUtil.isBean(Test1.class), "Test1 is annotated with @Repository");
        assertTrue(BeanUtil.isBean(Test2.class), "Test2 is annotated with @Service");
        assertTrue(BeanUtil.isBean(Test3.class), "Test3 is annotated with @WebController");
        assertFalse(BeanUtil.isBean(Test4.class), "Test4 is not a bean");
    }

    @Test
    void getName() {
        assertEquals("repo", BeanUtil.getBeanName(Test1.class));
        assertEquals("service", BeanUtil.getBeanName(Test2.class));
        assertEquals("test3", BeanUtil.getBeanName(Test3.class));
        assertEquals("test4", BeanUtil.getBeanName(Test4.class));
    }

    @Repository(name = "repo")
    class Test1 {
    }

    @Service(name = "service")
    class Test2 {
    }

    @WebController
    class Test3 {
    }

    class Test4 {
    }
}