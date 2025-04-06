package com.it;

import com.yawl.annotations.Service;

@Service
public class PingService {
    private final PingRepository repository;

    public PingService(PingRepository repository) {
        this.repository = repository;
    }

    public Pong get(String name) {
        return repository.getPing(name);
    }

    public void set(String name) {
        repository.setPing(name);
    }
}
