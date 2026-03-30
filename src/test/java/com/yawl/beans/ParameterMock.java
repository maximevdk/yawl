package com.yawl.beans;

import java.lang.reflect.Parameter;

class ParameterMock {
    public static Parameter parameter() throws NoSuchMethodException {
        return ParameterMock.class.getMethod("parameter1", Integer.class).getParameters()[0];
    }

    public void parameter1(Integer parameter) {
    }
}
