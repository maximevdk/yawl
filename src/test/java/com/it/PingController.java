package com.it;

import com.yawl.annotations.GetMapping;
import com.yawl.annotations.WebController;

@WebController(path = "ping")
public class PingController {

    private final PingService pingService;

    public PingController(PingService pingService) {
        this.pingService = pingService;
    }

    @GetMapping
    public Pong ping() {
        return pingService.ping();
    }
}
