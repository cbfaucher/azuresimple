package com.ms.wmbanking.azure.payment;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.ms.wmbanking.azure.Application;
import com.ms.wmbanking.azure.common.model.PaymentEvent;
import lombok.val;
import org.springframework.cloud.function.adapter.azure.AzureSpringBootRequestHandler;

import java.util.List;
import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class PaymentList extends AzureSpringBootRequestHandler<String, List<PaymentEvent>> {

    public PaymentList() {
        super(Application.class);
    }

    /**
     * This function listens at endpoint "/api/paymentList". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/paymentList
     * 2. curl {your host}/api/paymentList?name=HTTP%20Query
     */
    @FunctionName("paymentList")
    public List<PaymentEvent> run(
            @HttpTrigger(name = "req", methods = HttpMethod.GET, authLevel = AuthorizationLevel.ANONYMOUS) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("Fetching ALL PaymentEvents...");
        val list = handleRequest("", context);
        context.getLogger().info(String.format("...Found %d events", list.size()));
        return list;
    }
}
