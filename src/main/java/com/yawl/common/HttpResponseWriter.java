package com.yawl.common;

import com.yawl.http.model.ContentType;
import com.yawl.http.model.HttpResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Strategy interface for writing HTTP responses with a specific content type.
 */
public interface HttpResponseWriter extends Writer {
    /**
     * Returns whether this writer supports the given content type.
     *
     * @param contentType the content type to check
     * @return {@code true} if this writer handles the content type
     */
    boolean supports(ContentType contentType);

    /**
     * Writes the given HTTP response to the servlet response.
     *
     * @param response            the HTTP response to write
     * @param httpServletResponse the servlet response
     * @throws IOException if an I/O error occurs
     */
    void write(HttpResponse<?> response, HttpServletResponse httpServletResponse) throws IOException;
}
