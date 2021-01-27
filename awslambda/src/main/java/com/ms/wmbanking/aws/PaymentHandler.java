package com.ms.wmbanking.aws;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import lombok.val;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class PaymentHandler implements RequestHandler<Payment, AwsLambdaResponse> {

    @Override
    public AwsLambdaResponse handleRequest(Payment payment, Context context) {

        val result = String.format("(AWS RequestID=%s) Received payment: %s", context.getAwsRequestId(), payment.toString());

        context.getLogger().log(result);

        val response = new AwsLambdaResponse().withBody(result)
                                              .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);

        return response;
    }
}
