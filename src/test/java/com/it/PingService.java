package com.it;

import com.yawl.annotations.Service;

@Service
public class PingService {
    public Pong ping() {
        return new Pong("pong");
    }
}
