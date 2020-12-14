package com.ms.wmbanking.azure.payment;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.EventHubOutput;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.ms.wmbanking.azure.Application;
import com.ms.wmbanking.azure.model.Payment;
import com.ms.wmbanking.azure.model.PaymentEvent;
import lombok.val;
import org.springframework.cloud.function.adapter.azure.AzureSpringBootRequestHandler;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Azure Functions with HTTP Trigger.
 */
public class PaymentInitiate extends AzureSpringBootRequestHandler<Payment, PaymentEvent> {

    public PaymentInitiate() {
        super(Application.class);
    }

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
}
