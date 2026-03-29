package com.yawl.http;

import com.yawl.MockHttpServletRequest;
import com.yawl.exception.MissingPathParameterException;
import com.yawl.http.model.HttpMethod;
import com.yawl.http.model.PathPattern;
import com.yawl.http.model.Route;
import org.junit.jupiter.api.Test;

import static com.yawl.http.TestClass.parameter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PathParamArgumentResolverTest {
    private final PathParamArgumentResolver resolver = new PathParamArgumentResolver();

    @Test
    void supports() throws Exception {
        assertThat(resolver.supports(parameter("parameterWithQueryParam"))).isFalse();
        assertThat(resolver.supports(parameter("parameterWithPathParam"))).isTrue();
    }

    @Test
    void resolve_pathParamAvailable_returns() throws Exception {
        var request = new MockHttpServletRequest();
        request.setRequestUri("/test/test2");

        var route = new Route(HttpMethod.GET, PathPattern.parse("/test/{pathParam}"));
        var result = resolver.resolve(request, route, parameter("parameterWithPathParam"));
        assertThat(result).isEqualTo("test2");
    }

    @Test
    void resolve_pathParamNotAvailable_throws() throws Exception {
        var request = new MockHttpServletRequest();
        request.setRequestUri("/test");

        var route = new Route(HttpMethod.GET, PathPattern.parse("/test/{pathParam}"));
        assertThatThrownBy(() -> resolver.resolve(request, route, parameter("parameterWithPathParam")))
                .isInstanceOf(MissingPathParameterException.class);
    }
}