package com.yawl.http.client;

import com.yawl.http.model.HttpMethod;

import java.net.URI;
import java.util.List;
import java.util.Map;

public record HttpRequest(HttpMethod method, URI uri, List<Parameter> params, Map<String, String> headers, Object body) {
}
