package com.it;

import com.yawl.annotations.GetMapping;
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

    @GetMapping
    public Pong ping(@QueryParam(name = "name") String name) {
        return pingService.get(name);
    }

    @PostMapping(status = HttpStatus.ACCEPTED)
    public Pong setPing(@QueryParam(name = "name") String name) {
        pingService.set(name);
        return new Pong("POST " + name);
    }
}
