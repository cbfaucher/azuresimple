package com.ms.wmbanking.mixedapp.business;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentTest {

    private final Gson mapper = new GsonBuilder().setPrettyPrinting()
                                                 .create();

    @Test
    @SneakyThrows
    public void testtoFromJson() {
        val expected = new Payment(123L);

        val json = mapper.toJson(expected);
        System.out.println(json);

        val actual = mapper.fromJson(json, Payment.class);

        assertEquals(expected, actual);
    }
}