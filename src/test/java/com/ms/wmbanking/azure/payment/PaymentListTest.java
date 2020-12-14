package com.ms.wmbanking.azure.payment;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.ms.wmbanking.azure.Application;
import com.ms.wmbanking.azure.entities.EntityManagerFactoryHelper;
import com.ms.wmbanking.azure.entities.PaymentEntity;
import com.ms.wmbanking.azure.jackson.JsonHelper;
import com.ms.wmbanking.azure.model.PaymentEvent;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.Ignore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.lenient;

@SpringBootTest(classes = Application.class)
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@ActiveProfiles("test")
class PaymentListTest implements JsonHelper, EntityManagerFactoryHelper {

    private final ExecutionContext context = DummyExecutionContext.createFrom(PaymentList.class,
                                                                              "run", HttpRequestMessage.class,
                                                                              ExecutionContext.class);

    @Mock
    private HttpRequestMessage<Optional<String>> request;

    @Autowired
    @Getter
    private EntityManagerFactory entityManagerFactory;

    private List<PaymentEvent> expectedPayments;

    @BeforeEach
    @SneakyThrows
    void setUp() {
        expectedPayments = defaultObjectMapper.readValue(getClass().getResource("/data/payment-list.json"), paymentEventListTypeRef);
        execute(em -> {
            expectedPayments.forEach(e -> {
                em.persist(PaymentEntity.fromModel(e));
            });
            return null;
        });
    }

    @Test
    @SneakyThrows
    @Ignore
    public void testRun() {
        val fct = new PaymentList();

        lenient().when(request.getBody()).thenReturn(Optional.empty());

        val json = fct.run(request, context);
        val actualist = (List<PaymentEvent>) defaultObjectMapper.readValue(json, paymentEventListTypeRef);
        assertTrue(actualist.size() > 0);

        assertEquals(expectedPayments, actualist);

        System.out.println(json);
    }

}