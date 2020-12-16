package com.ms.wmbanking.azure.txnmanager;

import com.google.gson.GsonBuilder;
import com.ms.wmbanking.azure.common.entities.PaymentEntity;
import com.ms.wmbanking.azure.common.jackson.JsonHelper;
import com.ms.wmbanking.azure.common.model.Response;
import com.ms.wmbanking.azure.common.spring.TutorialConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.function.Function;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@SpringBootApplication
@Configuration
@Import({TxnManagerBeans.class, PaymentEntity.class})
@EntityScan(basePackageClasses = PaymentEntity.class)
@EnableTransactionManagement
@EnableConfigurationProperties
@ConfigurationPropertiesScan(basePackageClasses = TutorialConfiguration.class)
@Slf4j
public class Application implements ApplicationListener<ContextRefreshedEvent>, JsonHelper {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    private Environment environment;

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        log.info(String.format("Spring Boot Profiles: %s", String.join(", ", environment.getActiveProfiles())));
        log.info(String.format("Data Source URL is %s [Driver=%s]", dataSourceProperties.getUrl(), dataSourceProperties.determineDriverClassName()));
    }

    @Bean
    @Primary
    public GsonBuilder gsonBuilder() {
        return createGsonMapper().newBuilder();
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