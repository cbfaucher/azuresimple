package com.ms.wmbanking.azure.jackson;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.ms.wmbanking.azure.model.PaymentEvent;

import java.util.List;

public interface JsonHelper {

    ObjectMapper defaultObjectMapper = new JsonHelper() {}.createObjectMapper();

    CollectionLikeType paymentEventListTypeRef = defaultObjectMapper.getTypeFactory().constructCollectionLikeType(List .class, PaymentEvent .class);

    default ObjectMapper createObjectMapper() {
        return new ObjectMapper().enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                                 .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                                 .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                                 .enable(SerializationFeature.INDENT_OUTPUT)
                                 .findAndRegisterModules();
    }
}
