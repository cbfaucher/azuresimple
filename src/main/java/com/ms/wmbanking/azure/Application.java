package com.ms.wmbanking.azure;

import com.ms.wmbanking.azure.entities.PaymentEntity;
import com.ms.wmbanking.azure.model.Response;
import com.ms.wmbanking.azure.payment.PaymentBeans;
import com.ms.wmbanking.azure.txnmanager.TxnManagerBeans;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.function.Function;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@SpringBootApplication
@Configuration
@Import({PaymentBeans.class, TxnManagerBeans.class, PaymentEntity.class})
@EntityScan(basePackageClasses = PaymentEntity.class)
@EnableTransactionManagement
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean(name = "ping")
    public Function<String, Response> ping() {

        System.out.println("--> Bean 'ping' created");

        return s -> {
            System.out.println("--> Got a ping from: " + s);

            if (isNotBlank(s)) {
                return new Response(ACCEPTED, "Hello, " + s);
            } else {
                return new Response(BAD_REQUEST, "Please pass a name on the query string or in the request body");
            }
        };
    }
}