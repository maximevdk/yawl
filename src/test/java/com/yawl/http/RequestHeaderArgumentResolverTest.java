package com.yawl.http;

import com.yawl.MockHttpServletRequest;
import com.yawl.exception.MissingRequiredHeaderException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.yawl.http.TestClass.parameter;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestHeaderArgumentResolverTest {
    private final RequestHeaderArgumentResolver resolver = new RequestHeaderArgumentResolver();

    @Test
    void supports() throws NoSuchMethodException {
        assertThat(resolver.supports(parameter("parameterWithQueryParam"))).isFalse();
        assertThat(resolver.supports(parameter("parameterWithHeader"))).isTrue();
    }

    @Test
    void resolve_parameterAvailable_returns() throws Exception {
        var request = new MockHttpServletRequest();
        request.setHeaders(Map.of("test-header", "test"));

        var result = resolver.resolve(request, null, parameter("parameterWithHeader"));

        assertThat(result).isEqualTo("test");
    }

    @Test
    void resolve_parameterNotAvailable_notRequired_returnsNull() throws Exception {
        var request = new MockHttpServletRequest();
        request.setHeaders(Map.of("unavailable-header", "test"));

        var result = resolver.resolve(request, null, parameter("parameterWithHeader"));

        assertThat(result).isNull();
    }

    @Test
    void resolve_parameterNotAvailable_required_throws() throws Exception {
        var request = new MockHttpServletRequest();
        request.setHeaders(Map.of("unavailable-header", "test"));

        assertThatThrownBy(() -> resolver.resolve(request, null, parameter("parameterWithRequiredHeader")))
                .isInstanceOf(MissingRequiredHeaderException.class);
    }
}