package com.ms.wmbanking.aws;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.ms.wmbanking.azure.common.model.Payment;
import com.ms.wmbanking.azure.common.model.PaymentEvent;
import com.ms.wmbanking.azure.common.spring.ServerlessSpringHook;
import lombok.extern.slf4j.Slf4j;
import lombok.val;

@Slf4j
public class PaymentHandler extends ServerlessSpringHook<Payment, PaymentEvent> implements RequestHandler<Payment, AwsLambdaResponse> {

    public PaymentHandler() {
        super(Application.class);
    }

    @Override
    public AwsLambdaResponse handleRequest(Payment payment, Context context) {

        val result = String.format("(AWS RequestID=%s) Received payment: %s", context.getAwsRequestId(), payment.toString());
        context.getLogger().log(result);

        val event = handleRequest(payment,
                                  new Slf4j2AwsContextLoggerBridge(context.getLogger()),
                                  "addPayment");

        context.getLogger().log(String.format("Payment ID=%s processed successfully.  Returning Lambda's response...", event.getPaymentId()));

        return new AwsLambdaResponse().withBody(event)
                                      .withHeader("Content-Type", "application/json");
    }
}
