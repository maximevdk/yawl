package com.yawl.common;

import com.yawl.common.util.StringUtils;
import com.yawl.http.model.ContentType;
import com.yawl.http.model.HttpResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class TextPlainResponseWriter implements HttpResponseWriter {
    private static final ContentType CONTENT_TYPE = ContentType.TEXT_PLAIN;
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    @Override
    public boolean supports(ContentType contentType) {
        return CONTENT_TYPE.equals(contentType);
    }

    @Override
    public void write(HttpResponse<?> response, HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8);
        httpServletResponse.setContentType(CONTENT_TYPE.value());

        httpServletResponse.setCharacterEncoding(CHARSET);
        httpServletResponse.setContentType(CONTENT_TYPE.value());

        var outputStream = httpServletResponse.getOutputStream();
        outputStream.write(StringUtils.toString(response).getBytes(CHARSET));
        outputStream.flush();
        outputStream.close();
    }
}
