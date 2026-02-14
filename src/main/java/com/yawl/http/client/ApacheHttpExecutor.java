package com.yawl.http.client;

import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.net.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URISyntaxException;

public class ApacheHttpExecutor implements HttpExecutor {
    private static final Logger log = LoggerFactory.getLogger(ApacheHttpExecutor.class);

    private final JsonMapper jsonMapper;

    public ApacheHttpExecutor(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    @Override
    public <T> T execute(HttpRequest request, Type returnType) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            return client.execute(buildHttp(request), response -> handleResponse(response, returnType));
        } catch (IOException ex) {
            log.error("Unable to perform {}: {}", request.method(), request.uri(), ex);
            throw new RuntimeException("Unable to execute " + request.method(), ex);
        }
    }

    private HttpUriRequestBase buildHttp(HttpRequest request) {
        try {
            var http = switch (request.method()) {
                case GET -> new HttpGet(request.uri());
                case POST -> new HttpPost(request.uri());
                case DELETE -> new HttpDelete(request.uri());
            };

            var uri = new URIBuilder(request.uri());
            request.headers().forEach(http::addHeader);
            request.params().stream().filter(Parameter::isPathParam).forEach(parameter -> updatePathParam(uri, parameter));
            request.params().stream().filter(Parameter::isQueryParam).forEach(parameter -> uri.addParameter(parameter.name(), parameter.valueAsString()));

            // Body (if applicable)
            if (request.body() != null) {
                String json = jsonMapper.writeValueAsString(request.body());
                http.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
            }

            http.setUri(uri.build());
            return http;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void updatePathParam(URIBuilder uri, Parameter parameter) {
        var segments = uri.getPathSegments();
        for (int i = 0; i < segments.size(); i++) {
            var segment = segments.get(i);

            if (segment.matches("\\{" + parameter.name() + "}")) {
                segments.set(i, parameter.valueAsString());
                break;
            }

        }

        uri.setPathSegments(segments);
    }

    private <T> T handleResponse(ClassicHttpResponse response, Type returnType) {
        if (isNotFoundOrNoContent(response.getCode())) {
            return null;
        }

        if (is2xxSuccessful(response.getCode())) {
            try {
                return jsonMapper.readValue(response.getEntity().getContent(), jsonMapper.constructType(returnType));
            } catch (IOException ex) {
                log.error("Unable to read response {}", response.getEntity(), ex);
                throw new RuntimeException("Failed to get content", ex);
            }
        } else {
            throw new RuntimeException("Request failed with status code %s and reason %s".formatted(response.getCode(), response.getReasonPhrase()));
        }
    }

    private boolean is2xxSuccessful(int status) {
        return status == 200 || status == 202;
    }

    private boolean isNotFoundOrNoContent(int status) {
        return status == 404 || status == 204;
    }
}
