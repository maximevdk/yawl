package com.it;

import com.yawl.annotations.Service;

@Service
public class PingService {
    private final PingRepository repository;

    public PingService(PingRepository repository) {
        this.repository = repository;
    }

    public Pong ping(String name) {
        return repository.getPing(name);
    }
}
