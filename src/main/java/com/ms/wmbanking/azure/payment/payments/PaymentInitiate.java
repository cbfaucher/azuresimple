package com.ms.wmbanking.azure.payment.payments;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.annotation.*;
import com.ms.wmbanking.azure.payment.model.Payment;
import com.ms.wmbanking.azure.payment.model.PaymentEvent;
import lombok.val;
import org.springframework.cloud.function.adapter.azure.AzureSpringBootRequestHandler;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

/**
 * Azure Functions with HTTP Trigger.
 */
public class PaymentInitiate extends AzureSpringBootRequestHandler<Payment, PaymentEvent> {

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

        val payment = request.getBody();
        if (payment == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No Payment received");
        }

        val event = handleRequest(payment, context);
        context.getLogger().info("--> Returning Payment Event with ID: " + event.getPaymentId());
        return event;
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
