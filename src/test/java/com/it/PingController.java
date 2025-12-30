package com.it;

import com.yawl.annotations.GetMapping;
import com.yawl.annotations.PathParam;
import com.yawl.annotations.PostMapping;
import com.yawl.annotations.QueryParam;
import com.yawl.annotations.WebController;
import com.yawl.model.HttpStatus;

@WebController(path = "ping")
public class PingController {

    private final PingService pingService;

    public PingController(PingService pingService) {
        this.pingService = pingService;
    }

    @GetMapping(path = "/{id}")
    public Pong ping(@PathParam(name = "id") String id) {
        return pingService.get(id);
    }

    @GetMapping
    public Pong pingByQuery(@QueryParam(name = "id") String id) {
        return pingService.get(id);
    }

    @PostMapping(status = HttpStatus.ACCEPTED)
    public Pong setPing(@QueryParam(name = "name") String name) {
        return pingService.set(name);
    }
}
