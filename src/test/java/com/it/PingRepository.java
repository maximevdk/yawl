package com.it;

import com.yawl.annotations.Repository;
import com.yawl.database.InMemoryDatabase;

import java.util.Optional;
import java.util.UUID;

@Repository
public class PingRepository {
    private final InMemoryDatabase<String, Pong> pongDatabase;

    public PingRepository(InMemoryDatabase<String, Pong> pongDatabase) {
        this.pongDatabase = pongDatabase;
    }

    public Pong getPing(String id) {
        return pongDatabase.get(id);
    }

    public Pong setPing(String name) {
        var pong = new Pong(UUID.randomUUID().toString(), name);
        pongDatabase.store(pong.id(), pong);
        return pong;
    }

    public void updatePing(String id, String name) {
        Optional.ofNullable(getPing(id)).ifPresent(ping -> {
            pongDatabase.update(id, new Pong(id, name));
        });
    }

    public void delete(String id) {
        pongDatabase.delete(id);
    }
}
