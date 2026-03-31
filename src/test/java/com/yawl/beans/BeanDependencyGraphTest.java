package com.yawl.beans;

import com.yawl.beans.model.BeanDefinition;
import com.yawl.exception.DuplicateBeanException;
import com.yawl.exception.NoSuchBeanException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BeanDependencyGraphTest {
    @Test
    void validate_missingDependency_throws() throws Exception {
        var graph = new BeanDependencyGraph(new ApplicationContext());
        var dependency = new BeanDefinition("test2", Integer.class);
        var definition = new BeanDefinition("test", String.class, List.of(dependency), null);

        var thrown = assertThrows(NoSuchBeanException.class, () -> graph.validate(Set.of(definition)));
        assertEquals("No bean of type class java.lang.Integer found which class java.lang.String depends on", thrown.getMessage());
    }

    @Test
    void validate_missingDependency_dependencyInCtx_doesNotThrow() throws Exception {
        var ctx = new ApplicationContext();
        ctx.register("test1", 1, Integer.class);

        var graph = new BeanDependencyGraph(ctx);
        var dependency = new BeanDefinition("test1", Integer.class);
        var definition = new BeanDefinition("test", String.class, List.of(dependency), null);

        graph.validate(Set.of(definition));
    }

    @Test
    void validate_duplicateName_throws() throws Exception {
        var graph = new BeanDependencyGraph(new ApplicationContext());
        var definition1 = new BeanDefinition("test", String.class, List.of(), null);
        var definition2 = new BeanDefinition("test", Integer.class, List.of(), null);

        var thrown = assertThrows(DuplicateBeanException.class, () -> graph.validate(Set.of(definition1, definition2)));
        assertEquals("Bean with name test already exists", thrown.getMessage());
    }

    @Test
    void validate_valid_doesNotThrow() throws Exception {
        var graph = new BeanDependencyGraph(new ApplicationContext());
        var dependency = new BeanDefinition("test-dependency", Integer.class);
        var definition = new BeanDefinition("test", String.class, List.of(dependency), null);

        graph.validate(Set.of(definition, dependency));
    }

    @Test
    void getDefinitionByNameOrType() {
        var graph = new BeanDependencyGraph(new ApplicationContext());
        var definition = new BeanDefinition("test", String.class);
        graph.validate(Set.of(definition));

        assertEquals(definition, graph.getDefinitionByNameAndOrType("test", String.class));
    }

    @Test
    void getDefinitionByNameOrType_typeDoesntMatch_throws() {
        var graph = new BeanDependencyGraph(new ApplicationContext());
        var definition = new BeanDefinition("test", String.class);
        graph.validate(Set.of(definition));

        assertThrows(NoSuchBeanException.class, () -> graph.getDefinitionByNameAndOrType("test", Integer.class));
    }

    @Test
    void getDefinitionByType() {
        var graph = new BeanDependencyGraph(new ApplicationContext());
        var definition = new BeanDefinition("test", String.class);
        graph.validate(Set.of(definition));

        assertEquals(definition, graph.getDefinitionByType(String.class));
    }

    @Test
    void getDefinitionByType_moreThanOneOfType_throws() {
        var graph = new BeanDependencyGraph(new ApplicationContext());
        var definition1 = new BeanDefinition("test", String.class);
        var definition2 = new BeanDefinition("test2", String.class);
        graph.validate(Set.of(definition1, definition2));

        assertThrows(DuplicateBeanException.class, () -> graph.getDefinitionByType(String.class));
    }

    @Test
    void getDefinitionByType_noBeanOfType_throws() {
        var graph = new BeanDependencyGraph(new ApplicationContext());
        var definition = new BeanDefinition("test", String.class);
        graph.validate(Set.of(definition));

        assertThrows(NoSuchBeanException.class, () -> graph.getDefinitionByType(Integer.class));
    }

    @Test
    void getDefinitionByType_noBeans_throws() {
        var graph = new BeanDependencyGraph(new ApplicationContext());
        graph.validate(Set.of());

        assertThrows(NoSuchBeanException.class, () -> graph.getDefinitionByType(Integer.class));
    }
}