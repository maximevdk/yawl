package com.yawl.http;

import com.yawl.model.HttpMethod;

import java.net.URI;
import java.util.List;
import java.util.Map;

record HttpRequest(HttpMethod method, URI uri, List<Parameter> params, Map<String, String> headers, Object body) {
}
