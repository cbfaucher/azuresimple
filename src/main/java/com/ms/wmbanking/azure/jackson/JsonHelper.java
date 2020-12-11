package com.ms.wmbanking.azure.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public interface JsonHelper {

    default ObjectMapper createObjectMapper() {
        return new ObjectMapper().enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                                 .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                                 .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                                 .enable(SerializationFeature.INDENT_OUTPUT)
                                 .findAndRegisterModules();

    }
}
