package com.yawl.beans.isolated;

import com.yawl.annotations.Bean;
import com.yawl.annotations.Configuration;
import com.yawl.annotations.Configuration.Condition;
import com.yawl.annotations.HttpClient;
import com.yawl.annotations.Repository;
import com.yawl.annotations.Service;
import com.yawl.annotations.WebController;
import com.yawl.annotations.WebFilter;
import com.yawl.beans.BeanDiscoveryService;
import com.yawl.beans.model.BeanDefinition;
import com.yawl.configuration.Environment;
import com.yawl.configuration.model.PropertySource;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BeanDiscoveryServiceTest {
    private final BeanDiscoveryService service = new BeanDiscoveryService(new Environment(List.of(new MockPropertySource())));

    @Test
    void discoverAll() {
        var result = service.discoverAll(getClass().getPackage());
        assertThat(result).hasSize(7);
        assertThat(result).extracting(BeanDefinition::type)
                .map(Class::getName)
                .containsExactlyInAnyOrder(Test1.class.getName(), Test2.class.getName(), Test3.class.getName(), Test4.class.getName(), Test5.class.getName(), Test6.class.getName(), Test7.class.getName());

        var test3 = result.stream()
                .filter(definition -> definition.type() == Test3.class)
                .findFirst().orElseThrow();

        var test7 = result.stream()
                .filter(definition -> definition.type() == Test7.class)
                .findFirst().orElseThrow();

        assertThat(test3.dependencies()).hasSize(3);
        assertThat(test7.beanCreationMethod()).isNotNull();
    }

    @Test
    void discoverSet() {
        var input = Set.of(Test1.class, Test2.class, Test3.class, Test4.class, Test5.class, Test6.class);
        var result = service.discoverSet(input);

        assertThat(result).hasSize(7);
        assertThat(result).extracting(BeanDefinition::type)
                .map(Class::getName)
                .containsExactlyInAnyOrder(Test1.class.getName(), Test2.class.getName(), Test3.class.getName(), Test4.class.getName(), Test5.class.getName(), Test6.class.getName(), Test7.class.getName());

        var test3 = result.stream()
                .filter(definition -> definition.type() == Test3.class)
                .findFirst().orElseThrow();

        var test7 = result.stream()
                .filter(definition -> definition.type() == Test7.class)
                .findFirst().orElseThrow();

        assertThat(test3.dependencies()).hasSize(3);
        assertThat(test7.beanCreationMethod()).isNotNull();
    }

    @Test
    void discoverFromConfigClass_notAConfigClass_throws() {
        assertThatThrownBy(() -> service.discoverFromConfigClass(Test2.class))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("No config class provided");
    }

    @Test
    void discoverFromConfigClass_configClass_discoversMethodsAndConfigClass() {
        var result = service.discoverFromConfigClass(Test6.class);
        assertThat(result)
                .hasSize(2)
                .extracting(BeanDefinition::type)
                .extracting(Class::getName)
                .containsExactlyInAnyOrder(Test6.class.getName(), Test7.class.getName());
    }

    @Test
    void discoverFromConfigClass_configClass_conditionNotMet_returnsEmpty() {
        var result = service.discoverFromConfigClass(Test8.class);
        assertThat(result)
                .isEmpty();
    }

    @Repository(name = "repo")
    class Test1 {
    }

    @Service(name = "service")
    class Test2 {
    }

    @WebController
    class Test3 {
        private final Test1 test1;
        private final Test2 test2;

        public Test3() {
            test1 = null;
            test2 = null;
        }

        Test3(Test1 test1, Test2 test2) {
            this.test1 = test1;
            this.test2 = test2;
        }
    }

    @WebFilter
    class Test4 {
    }

    @HttpClient(name = "test", basePath = "/", url = "http://localhost:8080")
    interface Test5 {
    }

    @Configuration(condition = @Condition(property = "application.bean.enabled", value = "true"))
    class Test6 {
        @Bean
        public Test7 bean(Long dep) {
            return new Test7();
        }

        public Test7 ignored() {
            return new Test7();
        }
    }

    @Configuration(condition = @Condition(property = "application.web.enabled", value = "true"))
    class Test8 {
        @Bean
        public Test7 bean(Long dep) {
            return new Test7();
        }
    }

    class Test7 {
    }

    static class MockPropertySource implements PropertySource {
        @Override
        public String getProperty(String key) {
            if ("application.bean.enabled".equals(key)) {
                return "true";
            }

            return null;
        }
    }
}