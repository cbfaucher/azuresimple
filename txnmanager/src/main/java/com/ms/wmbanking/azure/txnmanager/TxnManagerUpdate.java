package com.ms.wmbanking.azure.txnmanager;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.Cardinality;
import com.microsoft.azure.functions.annotation.EventHubTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.ms.wmbanking.azure.common.model.PaymentEvent;
import com.ms.wmbanking.azure.common.spring.AzureFunctionSpringHook;

public class TxnManagerUpdate extends AzureFunctionSpringHook<PaymentEvent, Void> {

    public TxnManagerUpdate() {
        super(Application.class);
    }

    @FunctionName("txnmanagerUpdate")
    public void run(@EventHubTrigger(name = "event",
                                     eventHubName = "myhub", // blank because the value is included in the connection string
                                     cardinality = Cardinality.ONE,
                                     //consumerGroup = "MakeItSo",
                                     connection = "EventHubConnectionString")
                    final PaymentEvent event,
                    final ExecutionContext context) {

        context.getLogger().info("--> TxnManager to handler Payment: " + event.getPaymentId());

        handleRequest(event, context);
    }
}
