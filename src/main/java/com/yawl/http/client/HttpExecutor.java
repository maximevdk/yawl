package com.yawl.http.client;

import java.lang.reflect.Type;

interface HttpExecutor {
    <T> T execute(HttpRequest request, Type returnType);
}
