package com.ms.azure.cosmosdb;

import com.google.gson.GsonBuilder;
import com.ms.wmbanking.azure.common.jackson.JsonHelper;
import com.ms.wmbanking.azure.common.spring.AzureFunctionsSpringBeans;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@Configuration
@EnableMongoRepositories
@Import({CosmosBeans.class, AzureFunctionsSpringBeans.class})
@Slf4j
public class Application implements ApplicationListener<ContextRefreshedEvent>, JsonHelper {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    private Environment environment;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info(String.format("Spring Boot Profiles: %s", String.join(", ", environment.getActiveProfiles())));
    }

    @Bean
    @Primary
    public GsonBuilder gsonBuilder() {
        return createGsonMapper().newBuilder();
    }
}
