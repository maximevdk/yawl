package com.yawl.common;

import com.yawl.http.model.ContentType;
import com.yawl.http.model.HttpResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface HttpResponseWriter extends Writer {
    boolean supports(ContentType contentType);

    void write(HttpResponse<?> response, HttpServletResponse httpServletResponse) throws IOException;
}
