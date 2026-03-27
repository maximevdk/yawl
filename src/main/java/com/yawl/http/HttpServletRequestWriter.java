package com.yawl.http;

import com.yawl.common.HttpResponseWriter;
import com.yawl.http.model.ContentType;
import com.yawl.http.model.HttpResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

public class HttpServletRequestWriter {
    private final List<HttpResponseWriter> writers;
    private final HttpResponseWriter defaultWriter;

    public HttpServletRequestWriter(List<HttpResponseWriter> writers, HttpResponseWriter defaultWriter) {
        this.writers = writers;
        this.defaultWriter = defaultWriter;
    }

    public void write(HttpResponse<?> response, ContentType contentType, HttpServletResponse httpServletResponse) throws IOException {
        var writerForContentType = writers.stream()
                .filter(writer -> writer.supports(contentType))
                .findFirst()
                .orElse(defaultWriter);

        writerForContentType.write(response, httpServletResponse);
    }
}
