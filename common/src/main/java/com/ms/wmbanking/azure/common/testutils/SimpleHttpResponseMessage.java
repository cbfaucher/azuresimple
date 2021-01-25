package com.ms.wmbanking.azure.common.testutils;

import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatusType;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@With
public class SimpleHttpResponseMessage implements HttpResponseMessage, HttpResponseMessage.Builder {

    private HttpStatusType status;
    private Object body;
    private Map<String, String> headers = new HashMap<>();

    @Override
    public String getHeader(String key) {
        return headers.get(key);
    }

    @Override
    public Builder status(HttpStatusType status) {
        this.status = status;
        return this;
    }

    @Override
    public Builder header(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    @Override
    public Builder body(Object body) {
        this.body = body;
        return this;
    }

    @Override
    public HttpResponseMessage build() {
        return this;
    }
}
