package com.ms.wmbanking.azure.payment;

import com.ms.wmbanking.azure.model.Payment;
import com.ms.wmbanking.azure.model.PaymentEvent;
import lombok.AccessLevel;
import lombok.Setter;
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
public class PaymentBeans {

    @Autowired
    @Setter(value = AccessLevel.PACKAGE)
    private Supplier<String> paymentIdGenerator;

    @Bean
    public Function<Payment, PaymentEvent> paymentInitiate() {
        val paymentId = paymentIdGenerator.get();

        return p -> new PaymentEvent(paymentId, p, LocalDateTime.now());
    }

    @Bean
    public Supplier<String> paymentIdGenerator() {
        val random = new Random(System.currentTimeMillis());

        return () -> {
            val bytes = new byte[8];
            for (int i = 0; i < bytes.length; i++) {
                bytes[i] = (byte) (random.nextInt(26) + 'A');
            }
            return new String(bytes);
        };
    }

    @Bean
    public Function<String, List<PaymentEvent>> paymentList(final EntityManagerFactory entityManagerFactory) {
        return new PaymentList(entityManagerFactory);
    }
}
