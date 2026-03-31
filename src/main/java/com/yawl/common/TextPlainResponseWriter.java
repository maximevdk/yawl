package com.yawl.common;

import com.yawl.common.util.StringUtils;
import com.yawl.http.model.ContentType;
import com.yawl.http.model.HttpResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * {@link HttpResponseWriter} that writes HTTP responses as plain text.
 */
public class TextPlainResponseWriter implements HttpResponseWriter {
    private static final ContentType CONTENT_TYPE = ContentType.TEXT_PLAIN;
    private static final Charset CHARSET = StandardCharsets.UTF_8;

    @Override
    public boolean supports(ContentType contentType) {
        return CONTENT_TYPE.equals(contentType);
    }

    @Override
    public void write(HttpResponse<?> response, HttpServletResponse httpServletResponse) throws IOException {
        //TODO: right now we return the toString of an object, but that should maybe be avoided and return an error instead?
        httpServletResponse.setCharacterEncoding(CHARSET);
        httpServletResponse.setContentType(CONTENT_TYPE.value());

        var body = StringUtils.toString(response.body()).getBytes(CHARSET);
        var outputStream = httpServletResponse.getOutputStream();

        httpServletResponse.setContentLength(body.length);
        outputStream.write(body);
        outputStream.flush();
        outputStream.close();
    }
}
