package com.yawl.http;

import com.yawl.annotations.PathParam;
import com.yawl.annotations.QueryParam;
import com.yawl.annotations.RequestHeader;

import java.lang.reflect.Parameter;

class TestClass {
    public static Parameter parameter(String name) throws NoSuchMethodException {
        return TestClass.class.getMethod(name, String.class).getParameters()[0];
    }

    public void parameterWithHeader(@RequestHeader(name = "test-header", required = false) String header) {
    }

    public void parameterWithRequiredHeader(@RequestHeader(name = "test-header", required = true) String header) {
    }

    public void parameterWithQueryParam(@QueryParam(name = "testParam", required = true) String param) {
    }

    public void parameterWithPathParam(@PathParam(name = "pathParam") String param) {
    }
}
