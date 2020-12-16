package com.ms.wmbanking.azure.payment;

import com.ms.wmbanking.azure.common.model.Payment;
import com.ms.wmbanking.azure.common.model.PaymentEvent;
import lombok.AccessLevel;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.persistence.EntityManagerFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

@Configuration
@Slf4j
public class PaymentBeans {

    @Autowired
    @Setter(value = AccessLevel.PACKAGE)
    private Supplier<String> paymentIdGenerator;

    @Bean
    public Function<Payment, PaymentEvent> paymentInitiate() {
        return p -> {
            val paymentId = paymentIdGenerator.get();

            return new PaymentEvent(paymentId, p, PaymentEvent.Status.Initiating, LocalDateTime.now());
        };
    }

    @Bean
    public Random random() {
        return new Random(System.currentTimeMillis());
    }

    @Bean
    @Autowired
    public Supplier<String> paymentIdGenerator(final Random random) {
        return () -> {
            val bytes = new byte[8];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) (random.nextInt(26) + 'A');
            }
            val paymentId = new String(bytes);
            log.info("Generated new PaymentID: " + paymentId);
            return paymentId;
        };
    }

    @Bean
    public Function<String, List<PaymentEvent>> paymentList(final EntityManagerFactory entityManagerFactory) {
        return new PaymentListImpl(entityManagerFactory);
    }
}
