package com.it;

import com.yawl.annotations.GetMapping;
import com.yawl.annotations.WebController;

@WebController(path = "ping")
public class PingController {

    @GetMapping
    public String ping() {
        return "pong";
    }
}
