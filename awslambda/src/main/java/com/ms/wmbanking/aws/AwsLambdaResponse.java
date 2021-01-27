package com.ms.wmbanking.aws;

import lombok.*;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@With
@NoArgsConstructor
@Getter
@ToString
public class AwsLambdaResponse {
    private boolean isBase64Encoded = false;

    /**
     * @see HttpStatus
     */
    private int statusCode = HttpStatus.OK.value();

    private Map<String, String> headers = new HashMap<>();

    private Object body = null;

    public AwsLambdaResponse withHeader(final String headerName, final String value) {
        headers.put(headerName, value);
        return this;
    }
}
