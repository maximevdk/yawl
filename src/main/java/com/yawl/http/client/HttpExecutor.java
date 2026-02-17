package com.yawl.http.client;

import java.lang.reflect.Type;

public interface HttpExecutor {
    <T> T execute(HttpRequest request, Type returnType);
}
