package com.ms.wmbanking.aws;

import com.google.gson.Gson;
import com.ms.wmbanking.azure.common.jackson.JsonHelper;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentTest implements JsonHelper {

    private final Gson mapper = createGsonMapper();

    @Test
    @SneakyThrows
    public void testToFromJson() {
        val payment = new Payment("ABCD1234", 1.23D);

        val json = mapper.toJson(payment);

        System.out.println(json);

        val actual = mapper.fromJson(json, Payment.class);

        assertEquals(payment, actual);
    }
}