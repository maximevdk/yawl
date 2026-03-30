package com.yawl.common;

import com.yawl.MockHttpServletResponse;
import com.yawl.TestClass;
import com.yawl.http.model.ContentType;
import com.yawl.http.model.HttpResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TextPlainResponseWriterTest {

    private final TextPlainResponseWriter writer = new TextPlainResponseWriter();

    @Test
    void supports() {
        assertThat(writer.supports(ContentType.TEXT_PLAIN)).isTrue();
        assertThat(writer.supports(ContentType.APPLICATION_JSON)).isFalse();
    }

    @Test
    void write() throws Exception {
        var response = new MockHttpServletResponse();
        var data = HttpResponse.ok(new TestClass("test", 1, true, 0.9));

        writer.write(data, response);

        assertThat(response.getWrittenContent()).isEqualTo("TestClass{var1='test', var2=1, var3=true, var4=0.9}");
        assertThat(response.getContentLength()).isGreaterThan(0);
    }
}