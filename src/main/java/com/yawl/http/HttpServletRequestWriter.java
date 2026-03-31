package com.yawl.http;

import com.yawl.common.HttpResponseWriter;
import com.yawl.http.model.ContentType;
import com.yawl.http.model.HttpResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

/**
 * Delegates HTTP response writing to the appropriate {@link HttpResponseWriter} based on content type.
 */
public class HttpServletRequestWriter {
    private final List<HttpResponseWriter> writers;
    private final HttpResponseWriter defaultWriter;

    /**
     * Creates a new writer with the given response writers and a default fallback.
     *
     * @param writers       the available response writers
     * @param defaultWriter the fallback writer when no other matches
     */
    public HttpServletRequestWriter(List<HttpResponseWriter> writers, HttpResponseWriter defaultWriter) {
        this.writers = writers;
        this.defaultWriter = defaultWriter;
    }

    /**
     * Writes the given response using the writer matching the content type, or the default writer.
     *
     * @param response            the HTTP response
     * @param contentType         the target content type
     * @param httpServletResponse the servlet response
     * @throws IOException if an I/O error occurs
     */
    public void write(HttpResponse<?> response, ContentType contentType, HttpServletResponse httpServletResponse) throws IOException {
        var writerForContentType = writers.stream()
                .filter(writer -> writer.supports(contentType))
                .findFirst()
                .orElse(defaultWriter);

        writerForContentType.write(response, httpServletResponse);
    }
}
