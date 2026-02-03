package com.it;

import com.yawl.annotations.Autowired;
import com.yawl.database.InMemoryDatabase;
import com.yawl.test.annotation.YawlTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@YawlTest
class YawlApplicationTest {

    @Autowired
    private RestClient client;
    @Autowired
    private PingService service;
    @Autowired
    private PingRepository repository;
    @Autowired
    private InMemoryDatabase<String, Pong> database;
    @Autowired
    private PingController controller;

    @Test
    void contextLoads() {
        assertThat(client).isNotNull();
        assertThat(service).isNotNull();
        assertThat(repository).isNotNull();
        assertThat(database).isNotNull();
        assertThat(controller).isNotNull();
    }

    @Test
    void databaseWorks() {
        var pong = service.set("test");
        assertThat(pong).isNotNull();
        assertThat(pong).isEqualTo(service.get(pong.id()));
    }
}
