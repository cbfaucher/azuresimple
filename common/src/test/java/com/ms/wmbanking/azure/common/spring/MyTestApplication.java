package com.ms.wmbanking.azure.common.spring;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication()
@Configuration
public class MyTestApplication {

    @Bean
    public ApplicationContextSingletonBean contextSingletonBean() {
        return new ApplicationContextSingletonBean();
    }
}
