package com.ms.wmbanking.azure.txnmanager;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.ServiceBusTopicTrigger;
import com.ms.wmbanking.azure.common.model.PaymentEvent;
import com.ms.wmbanking.azure.common.spring.AzureFunctionSpringHook;

import javax.sound.midi.VoiceStatus;

/**
 * Azure Functions with HTTP Trigger.
 */
public class TxnManagerInitiate extends AzureFunctionSpringHook<PaymentEvent, VoiceStatus> {

    public TxnManagerInitiate() {
        super(Application.class);
    }

    /**
     * This function listens at endpoint "/api/TxnManagerInitiate". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/TxnManagerInitiate
     * 2. curl {your host}/api/TxnManagerInitiate?name=HTTP%20Query
     */
    @FunctionName("txnmanagerInitiate")
    public void initiate(
            @ServiceBusTopicTrigger(name = "eventFromBus", subscriptionName = "newpayments", connection= "ServiceBusConnectionString", topicName = "newpayment")
            final PaymentEvent event,
            final ExecutionContext context) {

        handleRequest(event, context);
    }
}
