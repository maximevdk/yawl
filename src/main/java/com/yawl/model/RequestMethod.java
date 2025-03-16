package com.yawl.model;

import java.util.List;

public record RequestMethod(String name, List<RequestParameter> parameters, MediaType produces) {
}
