package com.it;

import com.yawl.annotations.Autowired;
import com.yawl.test.annotation.YawlMvcTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@YawlMvcTest(controller = PingController.class, imports = {RestClient.class, ApplicationConfiguration.class})
public class PingControllerIT {
    @Autowired
    private RestClient restClient;

    @Autowired
    private PingRepository repository;

    @Test
    void getPong_path() {
        var pong = repository.setPing("test");
        var result = restClient.getByPath(pong.id());
        assertEquals(pong, result);
    }

    @Test
    void getPong_query() {
        var pong = repository.setPing("test");
        var result = restClient.getByQuery(pong.id());
        assertEquals(pong, result);
    }

    @Test
    void delete() {
        var pong = repository.setPing("test");

        assertNotNull(repository.getPing(pong.id()));
        restClient.delete(pong.id());
        assertNull(repository.getPing(pong.id()));
    }

    @Test
    void post() {
        var pong = restClient.post("test");

        assertNotNull(pong.id());
        assertEquals("test", pong.message());
        assertEquals(pong, repository.getPing(pong.id()));
    }
}
