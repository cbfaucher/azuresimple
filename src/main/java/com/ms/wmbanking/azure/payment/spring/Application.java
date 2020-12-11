package com.ms.wmbanking.azure.payment.spring;

import com.ms.wmbanking.azure.payment.model.Response;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.function.Function;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@SpringBootApplication
@Configuration
@Import(PaymentBeans.class)
public class Application {

    public static void main(String[] args) throws Exception {
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