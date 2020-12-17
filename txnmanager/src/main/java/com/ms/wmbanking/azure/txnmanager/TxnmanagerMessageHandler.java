package com.ms.wmbanking.azure.txnmanager;

import com.azure.messaging.eventgrid.EventGridEvent;
import com.azure.messaging.eventgrid.EventGridPublisherClient;
import com.azure.storage.queue.QueueClient;
import com.ms.wmbanking.azure.common.entities.PaymentEntity;
import com.ms.wmbanking.azure.common.hibernate.EntityManagerFactoryHelper;
import com.ms.wmbanking.azure.common.model.PaymentEvent;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.function.json.JsonMapper;

import javax.persistence.EntityManagerFactory;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.ms.wmbanking.azure.common.hibernate.EntityManagerFactoryHelper.now;

//  Created in TxnManagerBeans
@Slf4j
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class TxnmanagerMessageHandler implements EntityManagerFactoryHelper, Consumer<PaymentEvent> {

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    @Getter
    private EntityManagerFactory entityManagerFactory;

    @Autowired
    private QueueClient approvalQueue;

    @Autowired
    private EventGridPublisherClient executionEventGridClient;

    @Override
    public void accept(PaymentEvent paymentEvent) {

        if (paymentEvent == null) {
            log.error("Received a NULL event to save...");
            return;
        }

        switch (paymentEvent.getStatus()) {
            case Initiating:
                handleInitiating(paymentEvent);
                break;

            case Approved:
                handleApproved(paymentEvent);
                break;

            case Executed:
                handleExecuted(paymentEvent);
                break;

            default:
                log.info(String.format("Nothing to do with Payment %s [Status=%s]", paymentEvent.getPaymentId(), paymentEvent.getStatus()));
        }
    }

    private void handleInitiating(PaymentEvent paymentEvent) {

        //  keep track of receive status
        final PaymentEntity initiatedEntity = execute(em -> {

            val e = PaymentEntity.fromModel(paymentEvent);

            val exists = em.createQuery("SELECT COUNT(T) FROM PaymentEntity T WHERE T.paymentId=:paymentId", Long.class)
                           .setParameter("paymentId", e.getPaymentId())
                           .getSingleResult() > 0;

            if (!exists) {
                log.info(String.format("--> Saving new Payment %s to database [Status=%s]...", e.getPaymentId(), e.getStatus()));
                em.persist(e);
                return e;
            } else {
                log.warn(String.format("Payment %s ALREADY EXISTS!  Discarding this duplicate", e.getPaymentId()));
                return null;
            }
        });

        if (initiatedEntity == null) {
            return; //duplicate
        }

        //  flip to approval
        final PaymentEntity approvingEntity = initiatedEntity.withStatus(PaymentEvent.Status.Approving)
                                                             .withLastUpdated(now());
        execute(em -> {
            log.info(String.format("Updating database for Payment %s with Status=%s", approvingEntity.getPaymentId(), approvingEntity.getStatus()));
            em.merge(approvingEntity);
            return null;
        });

        //  send to approval
        //  Queues expect a Base64 encoded Strings... But the 'sendMessage' does not encode it, and no way to configure
        //  @QueueTrigger for that aspect either.  So stuck to manually do it.
        //  https://stackoverflow.com/questions/63023481/azure-functions-queue-trigger-is-expecting-base-64-messages-and-doesnt-process
        //  and
        //  https://github.com/Azure/azure-sdk-for-net/issues/10242#issuecomment-640862361
        val json = jsonMapper.toString(approvingEntity.toModel());
        log.info(String.format("Sending Payment %s for Approval...", approvingEntity.getPaymentId()));
        approvalQueue.sendMessage(Base64.encodeBase64String(json.getBytes()));
    }

    private void handleApproved(PaymentEvent paymentEvent) {
        //  keep track of receive status
        final PaymentEntity entity = execute(em -> {
            val e = PaymentEntity.fromModel(paymentEvent);
            log.info(String.format("Updating database for Payment %s with Status=%s", e.getPaymentId(), e.getStatus()));
            em.merge(e);
            return e;
        }).withStatus(PaymentEvent.Status.Executing)
          .withLastUpdated(now());

        //  Update DB
        execute(em -> {
            log.info(String.format("Updating database for Payment %s with Status=%s", entity.getPaymentId(), entity.getStatus()));
            em.merge(entity);
            return null;
        });

        val event = entity.toModel();
        log.info(String.format("Sending Payment to Execution:\n%s", jsonMapper.toString(event)));
        executionEventGridClient.sendEvents(Stream.of(event)
                                                  .map(e -> new EventGridEvent(e.getPaymentId(), "Execution", jsonMapper.toString(event), "1.0"))
                                                  .collect(Collectors.toList()));
    }

    private void handleExecuted(final PaymentEvent paymentEvent) {

        //  keep track of receive status
        execute(em -> {
            val e = PaymentEntity.fromModel(paymentEvent);
            log.info(String.format("Updating database for Payment %s with Status=%s", e.getPaymentId(), e.getStatus()));
            em.merge(e);
            return e;
        });

        log.info(String.format("Payment %s ALL DONE!", paymentEvent.getPaymentId()));
    }
}
