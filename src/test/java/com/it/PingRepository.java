package com.it;

import com.yawl.annotations.Repository;

@Repository
public class PingRepository {
    public String getPing() {
        return "pong";
    }
}
