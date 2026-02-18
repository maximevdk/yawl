package com.it;

import com.yawl.annotations.Service;

@Service
public class PingService {
    private final PingRepository repository;

    public PingService(PingRepository repository) {
        this.repository = repository;
    }

    public Pong get(String id) {
        return repository.getPing(id);
    }

    public Pong set(String name) {
        return repository.setPing(name);
    }

    public void update(String id, String name) {
        repository.updatePing(id, name);
    }

    public void delete(String id) {
        repository.delete(id);
    }
}
