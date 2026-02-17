package com.yawl.util.isolated.reflectionUtilTest;

import com.yawl.annotations.Repository;
import com.yawl.annotations.Service;
import com.yawl.annotations.TypedBean;
import com.yawl.annotations.WebController;
import com.yawl.util.ReflectionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReflectionUtilTest {

    @BeforeEach
    void setUp() {
        ReflectionUtil.init(ReflectionUtilTest.class);
    }

    @Test
    void getClassesAnnotatedWith() {
        var typedBeans = ReflectionUtil.getClassesAnnotatedWith(TypedBean.class);
        var services = ReflectionUtil.getClassesAnnotatedWith(Service.class);
        var repositories = ReflectionUtil.getClassesAnnotatedWith(Repository.class);
        var webControllers = ReflectionUtil.getClassesAnnotatedWith(WebController.class);

        assertEquals(Set.of(TestService.class, TestRepository.class, TestWebController.class), typedBeans);
        assertEquals(Set.of(TestWebController.class), webControllers);
        assertEquals(Set.of(TestRepository.class), repositories);
        assertEquals(Set.of(TestService.class), services);
    }

    @Service
    record TestService() {
    }

    @Repository
    record TestRepository() {
    }

    @WebController
    record TestWebController() {
    }
}