package com.yawl.http.client;

import com.yawl.http.model.HttpMethod;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

record HttpRequest(HttpMethod method, URI uri, List<Parameter> params, Map<String, String> headers, Object body) {
    Stream<Parameter> pathParameters() {
        return params.stream().filter(Parameter::isPathParam);
    }

    Stream<Parameter> queryParameters() {
        return params.stream().filter(Parameter::isQueryParam);
    }
}
