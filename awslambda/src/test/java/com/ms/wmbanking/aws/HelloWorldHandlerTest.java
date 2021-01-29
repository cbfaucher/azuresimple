package com.ms.wmbanking.aws;

import com.amazonaws.services.lambda.runtime.Context;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
class HelloWorldHandlerTest {

    @Test
    @SneakyThrows
    public void testHandler() {

        val dt = LocalDateTime.of(1973, 10, 31, 6, 30, 45);
        val handler = new HelloWorldHandler(() -> dt);

        val input = new HashMap<String, String>() {
            {
                put("firstName", "Christian");
            }
        };

        val response = handler.handleRequest(input, Mockito.mock(Context.class));

        assertEquals(200, response.getStatusCode());
        assertEquals("Hello Christian, it is 1973-10-31T06:30:45",
                     response.getBody());
    }
}