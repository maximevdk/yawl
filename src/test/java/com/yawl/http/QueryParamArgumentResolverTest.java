package com.yawl.http;

import com.yawl.MockHttpServletRequest;
import com.yawl.exception.MissingRequiredParameterException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.yawl.http.ParameterMock.parameter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QueryParamArgumentResolverTest {
    private final QueryParamArgumentResolver resolver = new QueryParamArgumentResolver();

    @Test
    void supports() throws Exception {
        assertThat(resolver.supports(parameter("parameterWithQueryParam"))).isTrue();
        assertThat(resolver.supports(parameter("parameterWithPathParam"))).isFalse();
    }

    @Test
    void resolve_queryParamAvailable_returns() throws Exception {
        var request = new MockHttpServletRequest();
        request.setParameters(Map.of("testParam", "test2"));

        var result = resolver.resolve(request, null, parameter("parameterWithQueryParam"));
        assertThat(result).isEqualTo("test2");
    }

    @Test
    void resolve_nonRequiredQueryParamUnAvailable_returnsNull() throws Exception {
        var request = new MockHttpServletRequest();

        var result = resolver.resolve(request, null, parameter("parameterWithOptionalQueryParam"));
        assertThat(result).isNull();
    }

    @Test
    void resolve_requiredQueryParamUnAvailable_throws() throws Exception {
        var request = new MockHttpServletRequest();

        assertThatThrownBy(() -> resolver.resolve(request, null, parameter("parameterWithQueryParam")))
                .isInstanceOf(MissingRequiredParameterException.class)
                .hasMessage("Required parameter 'testParam' not found");
    }

}