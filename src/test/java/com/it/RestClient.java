package com.it;

import com.yawl.annotations.GetMapping;
import com.yawl.annotations.HttpClient;
import com.yawl.annotations.PathParam;

@HttpClient(name = "restClient", url = "http://localhost:8080")
public interface RestClient {
    @GetMapping(path = "/ping/{id}")
    Pong getPing(@PathParam(name = "id") String id);
}
