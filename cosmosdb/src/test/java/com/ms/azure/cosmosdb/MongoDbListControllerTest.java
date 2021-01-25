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

import java.net.URI;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@DirtiesContext
class MongoDbListControllerTest {

    @Autowired
    private MongoProperties mongoProperties;

    @Autowired
    private MongoClient mongoClient;

    @Autowired
    private MongoCollection<AccountUpdate> collection;

    private final MongoDbListController controller = new MongoDbListController();

    @BeforeEach
    void setUp() {
        //  make sure it's clean
        collection.drop();
    }

    @Test
    @SneakyThrows
    public void testList() {
        assertEquals(0, collection.countDocuments());

        val au1 = new AccountUpdate("333-333333", 10L);
        val au2 = new AccountUpdate("444-444444", 15L);
        val au3 = new AccountUpdate("333-333333", 20L);

        collection.insertOne(au1);
        collection.insertOne(au2);
        collection.insertOne(au3);

        val actual = controller.list(new SimpleHttpRequestMessage<Void>().withUri(new URI("/api/list/333-333333")),
                                     new SimpleExecutionContext("mongoAccountNumberList"));

        val expected = Arrays.asList(au1, au3);

        assertEquals(expected, actual);
    }
}