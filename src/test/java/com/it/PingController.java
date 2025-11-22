package com.it;

import com.yawl.annotations.*;
import com.yawl.model.HttpStatus;

@WebController(path = "ping")
public class PingController {

    private final PingService pingService;

    public PingController(PingService pingService) {
        this.pingService = pingService;
    }

    @GetMapping(path = "/{id}")
    public Pong ping(@PathParam(name = "name") String id) {
        return pingService.get(id);
    }

    @PostMapping(status = HttpStatus.ACCEPTED)
    public Pong setPing(@QueryParam(name = "name") String name) {
        return pingService.set(name);
    }
}
