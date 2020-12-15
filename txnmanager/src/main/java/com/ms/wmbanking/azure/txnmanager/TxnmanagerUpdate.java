package com.ms.wmbanking.azure.txnmanager;

import com.ms.wmbanking.azure.common.model.PaymentEvent;
import com.ms.wmbanking.azure.common.hibernate.EntityManagerFactoryHelper;
import com.ms.wmbanking.azure.common.entities.PaymentEntity;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManagerFactory;
import java.util.function.Consumer;

@Component
@Slf4j
public class TxnmanagerUpdate implements EntityManagerFactoryHelper, Consumer<PaymentEvent> {

    @Autowired
    @Getter
    private EntityManagerFactory entityManagerFactory;

    @Override
    public void accept(PaymentEvent paymentEvent) {

        execute(em -> {
            val entity = PaymentEntity.fromModel(paymentEvent);
            if (entity != null) {
                log.info(String.format("--> Saving Payment %s to database...", paymentEvent.getPaymentId()));
                em.persist(entity);
            } else {
                log.warn("Received a NULL event to save...");
            }
            return null;
        });
    }
}
