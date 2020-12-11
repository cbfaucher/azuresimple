package com.ms.wmbanking.azure.txnmanager;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.Cardinality;
import com.microsoft.azure.functions.annotation.EventHubTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.ms.wmbanking.azure.model.PaymentEvent;
import org.springframework.cloud.function.adapter.azure.AzureSpringBootRequestHandler;

public class TxnManagerFunctions extends AzureSpringBootRequestHandler<PaymentEvent, Void> {

    @FunctionName("txnmanagerUpdate")
    public void run(@EventHubTrigger(name = "event",
                                     eventHubName = "myhub", // blank because the value is included in the connection string
                                     cardinality = Cardinality.ONE,
                                     //consumerGroup = "MakeItSo",
                                     connection = "EventHubConnectionString") final PaymentEvent event,
                    final ExecutionContext context) {

        context.getLogger().info("--> TxnManager to handler Payment: " + event.getPaymentId());

        handleRequest(event, context);
    }
}
