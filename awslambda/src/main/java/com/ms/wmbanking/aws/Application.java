package com.ms.wmbanking.aws;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.ms.wmbanking.azure.common.model.Payment;
import com.ms.wmbanking.azure.common.model.PaymentEvent;
import com.ms.wmbanking.azure.common.spring.ServerlessSpringBeans;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@Configuration
@Import({ServerlessSpringBeans.class, PaymentController.class})
@Slf4j
public class Application implements ApplicationListener<ContextRefreshedEvent> {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    private Environment environment;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info(String.format("Spring Boot Profiles: %s", String.join(", ", environment.getActiveProfiles())));
    }

    @Bean
    @Autowired
    MongoDatabase mongoDatabase(final MongoClient mongoClient, final MongoProperties properties) {
        return mongoClient.getDatabase(properties.getDatabase());
    }

    @Bean
    @Autowired
    public MongoCollection<PaymentEvent> mongoCollection(final MongoDatabase mongoDatabase) {

        val pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                                               fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        val collection = mongoDatabase.getCollection("AccountsCollection", PaymentEvent.class)
                                      .withCodecRegistry(pojoCodecRegistry);

        val accountIdxName = collection.createIndex(Indexes.ascending("accountNumber"),
                                                    new IndexOptions().unique(false)
                                                                      .name("AccountUpdateIdx"));
        log.info(String.format("Collection <AccountsCollection> with Index <%s> created successfully", accountIdxName));

        return collection;
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
    @Autowired
    public Function<Payment, PaymentEvent> addPayment(final Supplier<String> paymentIdGenerator,
                                                      final MongoCollection<PaymentEvent> collection) {
        return p -> {

            val paymentId = paymentIdGenerator.get();
            val event = new PaymentEvent(paymentId, p, PaymentEvent.Status.Initiating, LocalDateTime.now());

            log.info(String.format("Saving received Payment ID=%s to NoSQL Collection...", paymentId));
            collection.insertOne(event);

            return event;
        };
    }
}
