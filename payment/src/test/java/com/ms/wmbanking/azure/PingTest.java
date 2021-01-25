package com.ms.wmbanking.azure;

import com.ms.wmbanking.azure.common.testutils.SimpleExecutionContext;
import com.ms.wmbanking.azure.common.testutils.SimpleHttpRequestMessage;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class PingTest {

    @Test
    @SneakyThrows
    public void testPing() {

        val request = new SimpleHttpRequestMessage<Optional<String>>().withBody(Optional.of("Christian"));

        val ping = new Ping();
        val response = ping.ping(request, new SimpleExecutionContext("ping"));

        assertEquals("Hello, Christian", response.getBody());
    }
}