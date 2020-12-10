package com.ms.wmbanking.azure.payment.payments;

import java.time.LocalDateTime;
import java.util.*;
import com.microsoft.azure.functions.annotation.*;
import com.microsoft.azure.functions.*;
import com.ms.wmbanking.azure.payment.model.Payment;
import com.ms.wmbanking.azure.payment.model.PaymentEvent;

/**
 * Azure Functions with HTTP Trigger.
 */
public class PaymentInitiate {

    /**
     * This function listens at endpoint "/api/PaymentInitiate". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/PaymentInitiate
     * 2. curl {your host}/api/PaymentInitiate?name=HTTP%20Query
     */
    @FunctionName("paymentInitiate")
    @EventHubOutput(
            name = "request",
            eventHubName = "myhub", // blank because the value is included in the connection string
            connection = "EventHubConnectionString")
    public PaymentEvent run(
            @HttpTrigger(name = "req", methods = HttpMethod.POST, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Payment> request,
            final ExecutionContext context) {
        context.getLogger().info("--> Function 'paymentEvent' receiving a Payment initiation....");

        return new PaymentEvent("AAAAAAA", request.getBody(), LocalDateTime.now());
    }

    @FunctionName("echo")
    public void echoPaymentEvent(@EventHubTrigger(name = "event",
                                                  eventHubName = "myhub", // blank because the value is included in the connection string
                                                  cardinality = Cardinality.ONE,
                                                  //consumerGroup = "MakeItSo",
                                                  connection = "EventHubConnectionString") final PaymentEvent event,
                                 final ExecutionContext context) {

        context.getLogger().info("--> Got a new Event on EventHub: " + event.getPaymentId());
    }
}
