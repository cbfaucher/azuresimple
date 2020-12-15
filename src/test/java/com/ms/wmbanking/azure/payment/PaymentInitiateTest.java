package com.ms.wmbanking.azure.payment;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.ms.wmbanking.azure.jackson.JsonHelper;
import com.ms.wmbanking.azure.model.Account;
import com.ms.wmbanking.azure.model.Payment;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

//@SpringBootTest(classes = Application.class)
@ExtendWith({MockitoExtension.class})
//@ActiveProfiles("test")
class PaymentInitiateTest implements JsonHelper {

    private final ExecutionContext context = DummyExecutionContext.createFrom(PaymentInitiate.class,
                                                                              "run", HttpRequestMessage.class,
                                                                              ExecutionContext.class);

    @Mock
    private HttpRequestMessage<Payment> request;

    @Test
    @SneakyThrows
    public void testSuccess() {
        val fct = new PaymentInitiate();

        val payment = new Payment(12.45D,
                                new Account("Mom's Account", "123-456"),
                                new Account("Son's Account", "987-654"));

        when(request.getBody()).thenReturn(payment);

        val event = fct.run(request, context);

        assertNotNull(event.getPaymentId());
        assertSame(event.getPayment(), payment);
        assertNotNull(event.getEntryTimestamp());
    }
}