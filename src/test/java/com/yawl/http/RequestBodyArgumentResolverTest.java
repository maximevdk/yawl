package com.yawl.http;

import com.yawl.MockHttpServletRequest;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import static com.yawl.http.ParameterMock.parameter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestBodyArgumentResolverTest {
    private final RequestBodyArgumentResolver resolver = new RequestBodyArgumentResolver(JsonMapper.builder().build());

    @Test
    void supports() throws Exception {
        assertThat(resolver.supports(parameter("parameterWithBody"))).isTrue();
        assertThat(resolver.supports(parameter("parameterWithPathParam"))).isFalse();
    }

    @Test
    void resolve_bodyAvailable_returns() throws Exception {
        var request = new MockHttpServletRequest();
        request.setBody("\"test2\"");

        var result = resolver.resolve(request, null, parameter("parameterWithBody"));
        assertThat(result).isEqualTo("test2");
    }

    @Test
    void resolve_invalidBody_throws() throws Exception {
        var request = new MockHttpServletRequest();
        request.setBody("test2");

        assertThatThrownBy(() -> resolver.resolve(request, null, parameter("parameterWithBody")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Unable to parse body");
    }

    @Test
    void resolve_unavailableBody_throws() throws Exception {
        var request = new MockHttpServletRequest();

        assertThatThrownBy(() -> resolver.resolve(request, null, parameter("parameterWithBody")))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Unable to parse body");
    }
}