package com.ms.wmbanking.mixedapp.business;

import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

@Configuration
public class PaymentBeans {

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
            return new String(bytes);
        };
    }

    @Bean
    @Autowired
    public Function<Payment, PaymentEvent> initiate(final Supplier<String> paymentIdGenerator) {
        return p -> new PaymentEvent(paymentIdGenerator.get(), p.getAmount());
    }
}
