package com.it;

import com.yawl.annotations.DeleteMapping;
import com.yawl.annotations.GetMapping;
import com.yawl.annotations.HttpClient;
import com.yawl.annotations.PathParam;
import com.yawl.annotations.PostMapping;
import com.yawl.annotations.PutMapping;
import com.yawl.annotations.QueryParam;

@HttpClient(name = "restClient", url = "http://localhost:8080")
public interface RestClient {
    @GetMapping(path = "/ping/{id}")
    Pong getByPath(@PathParam(name = "id") String id);

    @GetMapping(path = "/ping")
    Pong getByQuery(@QueryParam(name = "id") String id);

    @PostMapping(path = "/ping")
    Pong post(@QueryParam(name = "name") String name);

    @PutMapping(path = "/ping/{id}")
    void put(@PathParam(name = "id") String id, @QueryParam(name = "name") String name);

    @DeleteMapping(path = "/ping/{id}")
    void delete(@PathParam(name = "id") String id);
}
