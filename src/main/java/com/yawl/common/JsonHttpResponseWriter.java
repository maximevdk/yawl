package com.yawl.common;

import com.yawl.http.model.ContentType;
import com.yawl.http.model.HttpResponse;
import jakarta.servlet.http.HttpServletResponse;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class JsonHttpResponseWriter implements HttpResponseWriter {
    private static final ContentType CONTENT_TYPE = ContentType.APPLICATION_JSON;
    private final JsonMapper jsonMapper;

    public JsonHttpResponseWriter(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public boolean supports(ContentType contentType) {
        return CONTENT_TYPE.equals(contentType);
    }

    @Override
    public void write(HttpResponse<?> response, HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setCharacterEncoding(StandardCharsets.UTF_8);
        httpServletResponse.setContentType(CONTENT_TYPE.value());

        var body = jsonMapper.writeValueAsBytes(response);
        httpServletResponse.setContentLength(body.length);
        var outputStream = httpServletResponse.getOutputStream();
        outputStream.write(body);
        outputStream.flush();
        outputStream.close();
    }
}
