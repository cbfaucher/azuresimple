package com.ms.wmbanking.azure.approval;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.EventHubOutput;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.QueueTrigger;
import com.ms.wmbanking.azure.common.model.PaymentEvent;

/**
 * Azure Functions with Queue Trigger.
 */
public class ApprovalApprove {
    /**
     */
    @FunctionName("approvalApprove")
    @EventHubOutput(
            name = "request",
            eventHubName = "myhub", // blank because the value is included in the connection string
            connection = "EventHubConnectionString")
    public PaymentEvent autoApproval(
            @QueueTrigger(name = "event", connection = "QueueConnectionString", queueName = "awaitingapproval", dataType = "") final PaymentEvent event,
            final ExecutionContext context) {
        if (event == null) {
            context.getLogger().warning("Event is NULL");
            throw new IllegalArgumentException("Event is NULL");
        }

        context.getLogger().info(String.format("AUto-Approving %s...", event.getPaymentId()));
        return event.withStatus(PaymentEvent.Status.Approved);
    }
}
