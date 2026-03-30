package com.it;

import com.yawl.annotations.Autowired;
import com.yawl.test.annotation.YawlMvcTest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@YawlMvcTest(controller = PingController.class, imports = {PingService.class, PingRepository.class, RestClient.class, ApplicationConfiguration.class})
public class PingControllerTest {
    @Autowired
    private RestClient restClient;

    @Autowired
    private PingRepository repository;

    @Test
    void getPong_path() {
        var pong = repository.setPing("test");
        var result = restClient.getByPath(pong.id());

        assertNotNull(result);
        assertEquals(pong, result);
    }

    @Test
    void getPong_query() {
        var pong = repository.setPing("test");
        var result = restClient.getByQuery(pong.id());

        assertNotNull(result);
        assertEquals(pong, result);
    }

    @Test
    void getPong_query_notFound() {
        var result = restClient.getByQuery(UUID.randomUUID().toString());
        assertNull(result);
    }

    @Test
    void getPongs_byIds_asList() {
        var pong1 = repository.setPing("test1");
        var pong2 = repository.setPing("test2");
        var pong3 = repository.setPing("test3");

        var result = restClient.getByIds(List.of(pong1.id(), pong2.id(), pong3.id()));

        assertFalse(result.isEmpty());
        assertEquals(3, result.size());
    }

    @Test
    void getPongs_byIds_asString() {
        var pong1 = repository.setPing("test1");
        var pong2 = repository.setPing("test2");
        var pong3 = repository.setPing("test3");

        var result = restClient.getByIds(String.join(",", pong1.id(), pong2.id(), pong3.id()));

        assertFalse(result.isEmpty());
        assertEquals(3, result.size());
    }

    @Test
    void getPongs_byIds_null() {
        var result = restClient.getByIds((String) null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
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

    @Test
    void post_withBody() {
        var pong = new Pong("123", "test");
        restClient.post(pong);
        assertEquals(pong, repository.getPing(pong.id()));
    }

    @Test
    void put() {
        var pong = repository.setPing("test");

        restClient.put(pong.id(), "updated value");

        assertEquals(new Pong(pong.id(), "updated value"), repository.getPing(pong.id()));
    }
}
