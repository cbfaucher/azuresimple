package com.ms.wmbanking.azure.txnmanager;

import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;
import com.ms.wmbanking.azure.common.entities.PaymentEntity;
import com.ms.wmbanking.azure.common.hibernate.EntityManagerFactoryHelper;
import com.ms.wmbanking.azure.common.model.PaymentEvent;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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

            default:
                log.info(String.format("Nothing to do with Payment %s [Status=%s]", paymentEvent.getPaymentId(), paymentEvent.getStatus()));
        }
    }

    private void handleInitiating(PaymentEvent paymentEvent) {

        //  save to database
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
        log.info(String.format("Sending Payment %s for Approval...", entity.getPaymentId()));
        approvalQueue.sendMessage(jsonMapper.toString(entity.toModel()));
    }
}
