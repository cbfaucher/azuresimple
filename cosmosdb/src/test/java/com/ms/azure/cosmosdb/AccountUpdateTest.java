package com.ms.azure.cosmosdb;

import com.google.gson.Gson;
import com.ms.wmbanking.azure.common.jackson.JsonHelper;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountUpdateTest implements JsonHelper {

    final private Gson mapper = createGsonMapper();

    @Test
    @SneakyThrows
    public void testToFromJson() {
        val o = new AccountUpdate("123-111111", 12.50D);

        val json = mapper.toJson(o);

        System.out.println(json);

        val actual = mapper.fromJson(json, AccountUpdate.class);

        assertEquals(o, actual);
    }
}