package com.yawl.http.client;

import com.yawl.annotations.DeleteMapping;
import com.yawl.annotations.GetMapping;
import com.yawl.annotations.HttpClient;
import com.yawl.annotations.PathParam;
import com.yawl.annotations.PostMapping;
import com.yawl.annotations.QueryParam;
import com.yawl.annotations.RequestBody;
import com.yawl.annotations.RequestHeader;
import com.yawl.http.model.HttpMethod;
import org.apache.hc.core5.net.URIBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

/**
 * This class serves as a proxy for {@code com.yawl.annotations.HttpClient} annotated interfaces.
 *
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

        var mappingDescriptor = MAPPINGS.stream()
                .filter(descriptor -> method.isAnnotationPresent(descriptor.annotationType()))
                .findFirst().orElseThrow();
        var request = new HttpRequest(mappingDescriptor.httpMethod(), getURI(method, mappingDescriptor), params, headers, body);
        return executor.execute(request, method.getGenericReturnType());
    }

    private URI getURI(Method method, MappingDescriptor<?> mappingDescriptor) throws URISyntaxException {
        var httpClient = method.getDeclaringClass().getAnnotation(HttpClient.class);
        return new URIBuilder(httpClient.url())
                .appendPath(httpClient.basePath())
                .appendPath(mappingDescriptor.getMethodPath(method))
                .build();
    }

    private static final List<MappingDescriptor<?>> MAPPINGS = List.of(
            new MappingDescriptor<>(GetMapping.class, GetMapping::path, HttpMethod.GET),
            new MappingDescriptor<>(PostMapping.class, PostMapping::path, HttpMethod.POST),
            new MappingDescriptor<>(DeleteMapping.class, DeleteMapping::path, HttpMethod.DELETE)
    );

    private record MappingDescriptor<A extends Annotation>(Class<A> annotationType, Function<A, String> pathExtractor, HttpMethod httpMethod) {
        public String getMethodPath(Method method) {
            var annotation = method.getAnnotation(annotationType);
            return pathExtractor.apply(annotation);
        }
    }
}
