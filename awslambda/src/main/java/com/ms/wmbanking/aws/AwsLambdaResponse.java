package com.ms.wmbanking.aws;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@With
@NoArgsConstructor
@Getter
@ToString
public class AwsLambdaResponse {
    private boolean isBase64Encoded = false;

    private int statusCode = 200;

    private Map<String, String> headers = new HashMap<>();

    private Object body = null;

    public AwsLambdaResponse withHeader(final String headerName, final String value) {
        headers.put(headerName, value);
        return this;
    }
}
