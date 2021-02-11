package com.ms.wmbanking.mixedapp.azure;

import com.microsoft.azure.functions.HttpMethod;
import com.ms.wmbanking.azure.common.testutils.SimpleExecutionContext;
import com.ms.wmbanking.azure.common.testutils.SimpleHttpRequestMessage;
import com.ms.wmbanking.mixedapp.business.Payment;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PaymentInitiateFunctionTest {

    private final PaymentInitiateFunction function = new PaymentInitiateFunction();

    @Test
    @SneakyThrows
    public void testInitiate() {

        val req = new SimpleHttpRequestMessage<Payment>(null, HttpMethod.GET, new Payment(456L));
        val context = new SimpleExecutionContext("initiate");

        val event = function.initiateViaHttp(req, context);

        assertNotNull(event.getPaymentId());
        assertEquals(req.getBody().getAmount(), event.getAmount());
    }
}