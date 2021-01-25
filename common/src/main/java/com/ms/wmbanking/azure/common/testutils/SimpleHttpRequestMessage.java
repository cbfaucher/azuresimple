package com.ms.wmbanking.azure.common.testutils;

import com.microsoft.azure.functions.*;
import lombok.*;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Getter
@With
public class SimpleHttpRequestMessage<T> implements HttpRequestMessage<T> {

    private final URI uri;
    private final HttpMethod httpMethod;

    @Singular
    private final Map<String, String> headers = new HashMap<>();

    @Singular
    private final Map<String, String> queryParameters = new HashMap<>();

    private final T body;

    public SimpleHttpRequestMessage() {
        this(null, null, null);
    }

    @Override
    public HttpResponseMessage.Builder createResponseBuilder(HttpStatus status) {
        return new SimpleHttpResponseMessage().withStatus(status);
    }

    @Override
    public HttpResponseMessage.Builder createResponseBuilder(HttpStatusType status) {
        return new SimpleHttpResponseMessage().withStatus(status);
    }
}
