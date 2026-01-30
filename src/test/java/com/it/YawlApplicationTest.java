package com.it;

import com.yawl.annotations.Autowired;
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

    @Test
    void contextLoads() {
        assertThat(client).isNotNull();
        assertThat(service).isNotNull();
        assertThat(repository).isNotNull();
    }
}
