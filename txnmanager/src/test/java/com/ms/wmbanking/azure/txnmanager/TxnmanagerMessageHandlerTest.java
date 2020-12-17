package com.ms.wmbanking.azure.txnmanager;

import com.azure.messaging.eventgrid.EventGridPublisherClient;
import com.azure.storage.queue.QueueClient;
import com.ms.wmbanking.azure.common.entities.PaymentEntity;
import com.ms.wmbanking.azure.common.jackson.JsonHelper;
import com.ms.wmbanking.azure.common.model.Account;
import com.ms.wmbanking.azure.common.model.Payment;
import com.ms.wmbanking.azure.common.model.PaymentEvent;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.function.json.GsonMapper;
import org.springframework.cloud.function.json.JsonMapper;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import java.time.LocalDateTime;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TxnmanagerMessageHandlerTest implements JsonHelper {

    private final JsonMapper jsonMapper = new GsonMapper(defaultGson);

    @Mock
    private EntityManagerFactory entityManagerFactory;

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Long> countQuery;

    private QueueClient queueClient;

    private EventGridPublisherClient eventGridPublisherClient;

    @Captor
    private ArgumentCaptor<PaymentEntity> paymentEntityCaptor;

    private TxnmanagerMessageHandler handler;

    @BeforeEach
    void setUp() {

        queueClient = Mockito.mock(QueueClient.class);
        eventGridPublisherClient = Mockito.mock(EventGridPublisherClient.class);

        lenient().when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);

        handler = new TxnmanagerMessageHandler(jsonMapper,
                                               entityManagerFactory,
                                               queueClient,
                                               eventGridPublisherClient) {
            @Override
            //  override this to avoid dealing with txn, etc.
            public <R, T> R execute(Function<EntityManager, R> fct) {
                return fct.apply(entityManager);
            }
        };
    }

    @Test
    @SneakyThrows
    public void testaccept_initiate_noDuplicate() {
        //  setup a non-existent paymentId
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);
        when(countQuery.setParameter(anyString(), any())).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(0L);

        val event = createPaymentEvent();

        handler.accept(event);

        //  verify initiated is saved
        verify(entityManager).persist(paymentEntityCaptor.capture());
        assertPaymentEntity(PaymentEvent.Status.Initiating);

        //  verify approval is saved
        verify(entityManager).merge(paymentEntityCaptor.capture());
        assertPaymentEntity(PaymentEvent.Status.Approving);

        //  verify event sent for approval
        verify(queueClient).sendMessage(anyString());
    }

    @Test
    @SneakyThrows
    public void testaccept_initiate_isDuplicate() {
        //  setup a non-existent paymentId
        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(countQuery);
        when(countQuery.setParameter(anyString(), any())).thenReturn(countQuery);
        when(countQuery.getSingleResult()).thenReturn(1L);

        val event = createPaymentEvent();

        handler.accept(event);

        //  verify initiated is saved
        verify(entityManager, never()).persist(paymentEntityCaptor.capture());
        //  verify approval is saved
        verify(entityManager, never()).merge(paymentEntityCaptor.capture());
        //  verify event sent for approval
        verify(queueClient, never()).sendMessage(anyString());
    }

    private void assertPaymentEntity(PaymentEvent.Status initiating) {
        val actualInitiated = paymentEntityCaptor.getValue();
        assertEquals("ABCD1234", actualInitiated.getPaymentId());
        assertEquals(initiating, actualInitiated.getStatus());
    }

    private PaymentEvent createPaymentEvent() {
        return new PaymentEvent("ABCD1234",
                                new Payment().withAmount(12.34D)
                                             .withFrom(new Account("Mom's", "12-34"))
                                             .withTo(new Account("Mine", "56.78")),
                                PaymentEvent.Status.Initiating,
                                LocalDateTime.now());
    }
}