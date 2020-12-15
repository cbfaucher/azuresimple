package com.ms.wmbanking.azure.payment;

import com.ms.wmbanking.azure.common.model.PaymentEvent;
import com.ms.wmbanking.azure.common.hibernate.EntityManagerFactoryHelper;
import com.ms.wmbanking.azure.common.entities.PaymentEntity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManagerFactory;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class PaymentListImpl implements EntityManagerFactoryHelper,
        Function<String, List<PaymentEvent>>,
        Supplier<List<PaymentEvent>> {

    @Getter
    private final EntityManagerFactory entityManagerFactory;

    @PostConstruct
    public void init() {
        log.info("--> paymentList bean created!");
    }

    @Override
    public List<PaymentEvent> apply(String s) {
        log.info(String.format("Chaining to apply(%s) --> get()", s));
        return get();
    }

    @Override
    public List<PaymentEvent> get() {
        log.info("Calling HBM to fetch the list of PaymentEvent...");
        return execute(em -> {

            log.info("Creating HBM Query for PaymentEntity...");
            return em.createQuery("SELECT T FROM PaymentEntity T", PaymentEntity.class)
                         .getResultList()
                         .stream()
                         .map(PaymentEntity::toModel)
                         .collect(Collectors.toList());
        });
    }
}
