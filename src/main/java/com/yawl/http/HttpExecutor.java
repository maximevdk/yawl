package com.yawl.http;

import java.lang.reflect.Type;

interface HttpExecutor {
    <T> T execute(HttpRequest request, Type returnType);
}
