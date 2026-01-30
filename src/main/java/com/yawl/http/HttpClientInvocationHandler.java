package com.yawl.http;

import com.yawl.annotations.GetMapping;
import com.yawl.annotations.HttpClient;
import com.yawl.annotations.PathParam;
import com.yawl.annotations.PostMapping;
import com.yawl.annotations.QueryParam;
import com.yawl.annotations.RequestBody;
import com.yawl.annotations.RequestHeader;
import com.yawl.model.HttpMethod;
import org.apache.hc.core5.net.URIBuilder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class serves as a proxy for {@code com.yawl.annotations.HttpClient} annotated interfaces.
 * @param executor
 */
public record HttpClientInvocationHandler(HttpExecutor executor) implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
       var params = new ArrayList<Parameter>();
        var headers = new HashMap<String, String>();
        Object body = null;

        var parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            var parameter = parameters[i];

            if (parameter.isAnnotationPresent(RequestBody.class)) {
                body = args[i];
                continue;
            }

            if (parameter.isAnnotationPresent(PathParam.class)) {
                params.add(Parameter.path(parameter.getName(), args[i]));
                continue;
            }

            if (parameter.isAnnotationPresent(QueryParam.class)) {
                params.add(Parameter.query(parameter.getName(), args[i]));
                continue;
            }

            if (parameter.isAnnotationPresent(RequestHeader.class)) {
                headers.put(parameter.getName(), (String) args[i]);
                continue;
            }
        }


        var request = new HttpRequest(getHttpMethod(method), getURI(method), params, headers, body);
        return executor.execute(request, method.getGenericReturnType());
    }

    private URI getURI(Method method) throws URISyntaxException {
        var httpClient = method.getDeclaringClass().getAnnotation(HttpClient.class);
        return new URIBuilder(httpClient.url())
                .appendPath(httpClient.basePath())
                .appendPath(getMethodPath(method))
                .build();
    }

    private String getMethodPath(Method method) {
        var getMapping =  method.getAnnotation(GetMapping.class);

        if (getMapping != null) {
            return getMapping.path();
        }

        return method.getAnnotation(PostMapping.class).path();
    }

    private HttpMethod getHttpMethod(Method method) {
        if (method.getAnnotation(GetMapping.class) != null) {
            return HttpMethod.GET;
        }

        return HttpMethod.POST;
    }
}
