package com.ms.wmbanking.azure.common.jackson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.ms.wmbanking.azure.common.model.PaymentEvent;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;

public interface JsonHelper {

    Gson defaultGson = new JsonHelper() {}.createGsonMapper();

    Type paymentEventListTypeRef = new TypeToken<List<PaymentEvent>>() {}.getType();

    default Gson createGsonMapper() {
        return new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeSerializer())
                                .setPrettyPrinting()
                                .create();
    }
}
