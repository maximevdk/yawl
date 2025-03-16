package com.it;

import com.yawl.annotations.Repository;

@Repository
public class PingRepository {
    public Pong getPing(String name) {
        return new Pong(name);
    }
}
