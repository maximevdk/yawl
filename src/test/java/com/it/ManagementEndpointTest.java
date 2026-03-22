package com.it;

import com.yawl.TomcatWebServer;
import com.yawl.annotations.Autowired;
import com.yawl.http.model.HttpResponse;
import com.yawl.test.annotation.LocalTestPort;
import com.yawl.test.annotation.YawlTest;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@YawlTest
public class ManagementEndpointTest {
    @Autowired
    private JsonMapper jsonMapper;
    @Autowired
    private TomcatWebServer webServer;
    @LocalTestPort
    private int port;

    @Test
    void testManagementEndpoint() throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            var response = client.execute(new HttpGet("http://localhost:%d/health".formatted(port)), this::map);

            assertThat(response.status().is2xxSuccessful()).isTrue();
            assertThat(((JsonNode) response.body()).get("status").stringValue()).isEqualTo("UP");
            assertThat(((JsonNode) response.body()).get("debug")).isNotNull();
            assertThat(((JsonNode) response.body()).get("health")).isNotNull();
        }
    }

    private HttpResponse<?> map(ClassicHttpResponse response) throws IOException {
        return switch (response.getCode()) {
            case 200 -> HttpResponse.ok(jsonMapper.readTree(response.getEntity().getContent()));
            case 404 -> HttpResponse.notFound("Not Found");
            case 500 -> HttpResponse.internal();
            default -> throw new IllegalStateException("Unexpected value: " + response.getCode());
        };
    }
}
