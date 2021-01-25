package com.ms.azure.cosmosdb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.ms.wmbanking.azure.common.testutils.SimpleExecutionContext;
import com.ms.wmbanking.azure.common.testutils.SimpleHttpRequestMessage;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@DirtiesContext
class MongoDbAddControllerTest {

    @Autowired
    private MongoProperties mongoProperties;

    @Autowired
    private MongoClient mongoClient;

    private MongoCollection collection;

    private final MongoDbAddController controller = new MongoDbAddController();

    @BeforeEach
    void setUp() {
        collection = mongoClient.getDatabase(mongoProperties.getDatabase())
                                .getCollection("AccountsCollection");
        collection.drop();
    }

    @Test
    @SneakyThrows
    public void testAdd() {
        assertEquals(0, collection.countDocuments());

        val accountUpdate = new AccountUpdate("222-222222", 10L);
        controller.add(new SimpleHttpRequestMessage<AccountUpdate>().withBody(accountUpdate),
                       new SimpleExecutionContext("mongoAccountUpdateAdd"));

        //  no exception

        assertEquals(1, collection.countDocuments());
    }
}