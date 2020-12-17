package com.ms.wmbanking.azure.payment;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.ms.wmbanking.azure.common.model.PaymentEvent;
import com.ms.wmbanking.azure.common.spring.AzureFunctionSpringHook;
import lombok.val;

import java.util.List;
import java.util.Optional;

/**
 * Azure Functions with HTTP Trigger.
 */
public class PaymentList extends AzureFunctionSpringHook<String, List<PaymentEvent>> {

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
