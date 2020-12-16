package com.ms.wmbanking.azure.txnmanager;

import com.azure.core.util.Configuration;
import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;
import com.ms.wmbanking.azure.common.entities.PaymentEntity;
import com.ms.wmbanking.azure.common.hibernate.EntityManagerFactoryHelper;
import com.ms.wmbanking.azure.common.model.PaymentEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.function.json.JsonMapper;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import java.util.function.Consumer;

import static com.ms.wmbanking.azure.common.hibernate.EntityManagerFactoryHelper.now;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Component("txnmanagerUpdate")
@Slf4j
public class TxnmanagerDatabaseUpdate implements EntityManagerFactoryHelper, Consumer<PaymentEvent> {

    @Autowired
    private Environment environment;

    @Autowired
    private JsonMapper jsonMapper;

    @Autowired
    @Getter
    private EntityManagerFactory entityManagerFactory;

    private QueueClient approvalQueue;

    @PostConstruct
    public void initialize() {
        val connStr = environment.getProperty("QueueConnectionString");
        if (isBlank(connStr)) {
            throw new IllegalArgumentException("Value missing for 'QueueConnectionString'.");
        }

        approvalQueue = new QueueClientBuilder().connectionString(connStr)
                                                .queueName("awaitingapproval")
                                                .buildClient();
    }

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

            default:
                log.info(String.format("Nothing to do with Payment %s [Status=%s]", paymentEvent.getPaymentId(), paymentEvent.getStatus()));
        }
    }

    private void handleInitiating(PaymentEvent paymentEvent) {

        //  keep track of receive status
        final PaymentEntity entity = execute(em -> {
            val e = PaymentEntity.fromModel(paymentEvent);
            log.info(String.format("--> Saving new Payment %s to database [Status=%s]...", e.getPaymentId(), e.getStatus()));
            em.persist(e);
            return e;
        }).withStatus(PaymentEvent.Status.Approving)
          .withLastUpdated(now());

        //  flip to approval
        execute(em -> {
            log.info(String.format("Updating database for Payment %s with Status=%s", entity.getPaymentId(), entity.getStatus()));
            em.merge(entity);
            return null;
        });

        //  send to approval
        //  Queues expect a Base64 encoded Strings... But the 'sendMessage' does not encode it, and no way to configure
        //  @QueueTrigger for that aspect either.  So stuck to manually do it.
        //  https://stackoverflow.com/questions/63023481/azure-functions-queue-trigger-is-expecting-base-64-messages-and-doesnt-process
        //  and
        //  https://github.com/Azure/azure-sdk-for-net/issues/10242#issuecomment-640862361
        val json = jsonMapper.toString(entity.toModel());
        log.info(String.format("Sending Payment %s for Approval...", entity.getPaymentId()));
        approvalQueue.sendMessage(Base64.encodeBase64String(json.getBytes()));
    }

    private void handleApproved(PaymentEvent paymentEvent) {

        //  keep track of receive status
        val entity = execute(em -> {
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

        //todo: send to event grid
    }
}
