package com.ms.azure.execution;

import com.google.gson.JsonSyntaxException;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.EventGridTrigger;
import com.microsoft.azure.functions.annotation.EventHubOutput;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.ms.wmbanking.azure.common.azure.EventMessage;
import com.ms.wmbanking.azure.common.jackson.JsonHelper;
import com.ms.wmbanking.azure.common.model.PaymentEvent;
import lombok.val;

/**
 * Azure Functions with HTTP Trigger.
 */
public class ExecutionExecute implements JsonHelper {
    /**
     * This function listens at endpoint "/api/executionExecute". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/executionExecute
     * 2. curl {your host}/api/executionExecute?name=HTTP%20Query
     */
    @FunctionName("executionExecute")
    @EventHubOutput(
            name = "request",
            eventHubName = "myhub", // blank because the value is included in the connection string
            connection = "EventHubConnectionString")
    public PaymentEvent execute(
            @EventGridTrigger(name = "EventGridEndpoint") final EventMessage msg,
            final ExecutionContext context) {

        context.getLogger().info(String.format("ExecutionExecute.execute received Message:BEGIN>>%s<<END", msg.toString()));

        try {
            val event = msg.getAs(defaultGson, PaymentEvent.class);

            context.getLogger().info("Executing Payment: " + event.getPaymentId());

            return event.withStatus(PaymentEvent.Status.Executed);
        } catch (JsonSyntaxException e) {
            context.getLogger().severe(String.format("EXCEPTION <%s>: %s", e.getClass().getSimpleName(), e.getMessage()));
            return null;
        }
    }
}
