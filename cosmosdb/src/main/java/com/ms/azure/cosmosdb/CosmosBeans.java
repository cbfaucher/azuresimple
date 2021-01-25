package com.ms.azure.cosmosdb;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Configuration
@Slf4j
public class CosmosBeans {

    @Bean
    @Autowired MongoDatabase mongoDatabase(final MongoClient mongoClient, final MongoProperties properties) {
        return mongoClient.getDatabase(properties.getDatabase());
    }

    @Bean
    @Autowired
    public MongoCollection<AccountUpdate> mongoCollection(final MongoDatabase mongoDatabase) {

        val pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                                               fromProviders(PojoCodecProvider.builder().automatic(true).build()));

        val collection = mongoDatabase.getCollection("AccountsCollection", AccountUpdate.class)
                                      .withCodecRegistry(pojoCodecRegistry);

        val accountIdxName = collection.createIndex(Indexes.ascending("accountNumber"),
                                                    new IndexOptions().unique(false)
                                                                      .name("AccountUpdateIdx"));
        log.info(String.format("Collection <AccountsCollection> with Index <%s> created successfully", accountIdxName));

        return collection;
    }

    @Bean
    @Autowired
    public Consumer<AccountUpdate> mongoAccountUpdateAdd(final MongoCollection<AccountUpdate> collection) {
        return au -> {
            val result = collection.insertOne(au);

            System.out.printf("--> Added AccountUpdate successfully for '%s' to Mongo with Result=%s%n",
                              au.getAccountNumber(),
                              result);
        };
    }

    @Bean
    @Autowired
    public Function<String, List<AccountUpdate>> mongoAccountNumberList(final MongoCollection<AccountUpdate> collection) {
        return accountNumber -> {
            System.out.println("--> Looking for AccountNumber: " + accountNumber);

            BasicDBObject inQuery = new BasicDBObject("accountNumber", accountNumber);

            val found = collection.find(inQuery, AccountUpdate.class);

            return found.into(new ArrayList<>());
        };
    }
}
