package com.yawl.beans;

import com.yawl.annotations.Repository;
import com.yawl.annotations.Service;
import com.yawl.annotations.WebController;
import com.yawl.exception.DuplicateBeanException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ApplicationContextTest {

    @ParameterizedTest
    @MethodSource("provideArguments")
    void register_nameAndInstance_registersSuperClassAndInterfaces(Object bean, List<Class<?>> expected) {
        var ctx = new ApplicationContext();
        ctx.register("test", bean);

        expected
                .forEach(expectedClass -> assertNotNull(ctx.getBeanByTypeOrThrow(expectedClass)));
    }

    @Test
    void getBeansAnnotatedWith() {
        var ctx = new ApplicationContext();
        ctx.register("service1", new Bean1());
        ctx.register("service2", new Bean2());
        ctx.register("service3", new Bean3());
        ctx.register("repository1", new Bean4());
        ctx.register("nothing", new Bean5());

        assertThat(ctx.getBeansAnnotatedWith(Service.class))
                .containsExactlyInAnyOrder(Bean1.class, Bean2.class, Bean3.class);
        assertThat(ctx.getBeansAnnotatedWith(Repository.class))
                .containsExactlyInAnyOrder(Bean4.class);
        assertThat(ctx.getBeansAnnotatedWith(WebController.class))
                .isEmpty();
    }

    @Test
    void findBeansByType() {
        var ctx = new ApplicationContext();
        var sameTypeBean1 = new Bean4();
        var sameTypeBean2 = new Bean6();
        ctx.register("sameType1", sameTypeBean1);
        ctx.register("sameType2", sameTypeBean2);

        assertThat(ctx.findBeansByType(Test2.class))
                .containsExactlyInAnyOrder(sameTypeBean1, sameTypeBean2);
        assertThat(ctx.findBeansByType(Test3.class))
                .containsExactlyInAnyOrder(sameTypeBean1, sameTypeBean2);
        assertThat(ctx.findBeansByType(SuperClass.class))
                .containsExactlyInAnyOrder(sameTypeBean2);
    }

    @Test
    void findBeanByTypeOrThrow() {
        var ctx = new ApplicationContext();
        var sameTypeBean1 = new Bean4();
        var sameTypeBean2 = new Bean6();
        ctx.register("sameType1", sameTypeBean1);
        ctx.register("sameType2", sameTypeBean2);

        assertThrows(DuplicateBeanException.class, () -> ctx.getBeanByTypeOrThrow(Test2.class));
        assertThrows(DuplicateBeanException.class, () -> ctx.getBeanByTypeOrThrow(Test3.class));
        assertThat(ctx.getBeanByTypeOrThrow(SuperClass.class)).isNotNull();
    }

    public static Stream<Arguments> provideArguments() {
        return Stream.of(
                Arguments.of(new Bean1(), List.of(Bean1.class, SuperClass.class, Test1.class, Test2.class)),
                Arguments.of(new Bean2(), List.of(Bean2.class, Test2.class)),
                Arguments.of(new Bean3(), List.of(Bean3.class, Test3.class, Test1.class)),
                Arguments.of(new Bean4(), List.of(Bean4.class, Test3.class, Test2.class, Test1.class)),
                Arguments.of(new Bean5(), List.of(Bean5.class, SuperClass.class, Test2.class)),
                Arguments.of(new Bean6(), List.of(Bean6.class, SuperClass.class, Test1.class, Test2.class, Test3.class))
        );
    }

    interface Test1 {
    }

    interface Test2 {
    }

    interface Test3 extends Test1 {
    }

    static class SuperClass implements Test2 {
    }

    @Service
    static class Bean1 extends SuperClass implements Test1 {
    }

    @Service
    static class Bean2 implements Test2 {
    }

    @Service
    static class Bean3 implements Test3 {
    }

    @Repository
    static class Bean4 implements Test2, Test3 {
    }

    static class Bean5 extends SuperClass {
    }

    static class Bean6 extends SuperClass implements Test1, Test2, Test3 {
    }
}