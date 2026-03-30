package com.yawl.common;

import com.yawl.MockHttpServletResponse;
import com.yawl.TestClass;
import com.yawl.http.model.ContentType;
import com.yawl.http.model.HttpResponse;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class JsonHttpResponseWriterTest {
    private final JsonHttpResponseWriter writer = new JsonHttpResponseWriter(new JsonMapper());

    @Test
    void supports() {
        assertThat(writer.supports(ContentType.TEXT_PLAIN)).isFalse();
        assertThat(writer.supports(ContentType.APPLICATION_JSON)).isTrue();
    }

    @Test
    void write() throws Exception {
        var response = new MockHttpServletResponse();
        var data = HttpResponse.ok(new TestClass("test", 1, true, 0.9));

        writer.write(data, response);

        assertThat(response.getWrittenContent()).isEqualTo("{\"var1\":\"test\",\"var2\":1,\"var3\":true,\"var4\":0.9}");
        assertThat(response.getContentLength()).isGreaterThan(0);
    }
}