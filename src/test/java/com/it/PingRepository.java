package com.it;

import com.yawl.annotations.Repository;
import com.yawl.database.InMemoryDatabase;

@Repository
public class PingRepository {
    private final InMemoryDatabase<String, Pong> pongDatabase;

    public PingRepository(InMemoryDatabase<String, Pong> pongDatabase) {
        this.pongDatabase = pongDatabase;
    }

    public Pong getPing(String name) {
        return pongDatabase.get(name);
    }

    public void setPing(String name) {
        pongDatabase.store(name, new Pong(name));
    }
}
