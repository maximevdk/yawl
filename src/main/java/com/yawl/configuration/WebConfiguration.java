package com.yawl.configuration;

import com.yawl.annotations.Bean;
import com.yawl.annotations.Configuration;
import com.yawl.beans.CommonBeans;
import com.yawl.common.JsonHttpResponseWriter;
import com.yawl.common.TextPlainResponseWriter;
import com.yawl.exception.BadRequestExceptionResolver;
import com.yawl.exception.RouteNotFoundExceptionResolver;
import com.yawl.http.PathParamArgumentResolver;
import com.yawl.http.QueryParamArgumentResolver;
import com.yawl.http.RequestBodyArgumentResolver;
import com.yawl.http.RequestHeaderArgumentResolver;
import com.yawl.http.ServletRequestArgumentResolver;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

@Configuration
public class WebConfiguration {

    @Bean
    public RouteNotFoundExceptionResolver routeNotFoundExceptionResolver() {
        return new RouteNotFoundExceptionResolver();
    }

    @Bean
    public BadRequestExceptionResolver badRequestExceptionResolver() {
        return new BadRequestExceptionResolver();
    }

    @Bean
    public TextPlainResponseWriter textPlainResponseWriter() {
        return new TextPlainResponseWriter();
    }

    @Bean
    public JsonHttpResponseWriter jsonHttpResponseWriter(JsonMapper jsonMapper) {
        return new JsonHttpResponseWriter(jsonMapper);
    }

    @Bean(name = CommonBeans.REQUEST_PARAMETER_ARGUMENT_RESOLVER)
    public ServletRequestArgumentResolver servletRequestArgumentResolver(JsonMapper jsonMapper) {
        var pathParamArgumentResolver = new PathParamArgumentResolver();
        var queryParamArgumentResolver = new QueryParamArgumentResolver();
        var requestHeaderArgumentResolver = new RequestHeaderArgumentResolver();
        var requestBodyArgumentResolver = new RequestBodyArgumentResolver(jsonMapper);

        return new ServletRequestArgumentResolver(List.of(
                pathParamArgumentResolver,
                queryParamArgumentResolver,
                requestHeaderArgumentResolver,
                requestBodyArgumentResolver
        ));
    }
}
