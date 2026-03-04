package com.yawl.exception;

import com.yawl.http.model.HttpResponse;

import java.util.List;

public class CompositeExceptionResolver implements ExceptionResolver {
    private final List<ExceptionResolver> resolvers;

    public CompositeExceptionResolver(List<ExceptionResolver> resolvers) {
        this.resolvers = resolvers;
    }

    @Override
    public HttpResponse<?> resolve(Throwable exception) {
        for (ExceptionResolver resolver : resolvers) {
            var response = resolver.resolve(exception);

            if (response != null) {
                return response;
            }
        }

        return null;
    }
}
