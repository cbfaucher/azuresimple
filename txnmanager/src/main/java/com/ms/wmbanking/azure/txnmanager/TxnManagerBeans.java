package com.ms.wmbanking.azure.txnmanager;

import com.azure.core.credential.AzureKeyCredential;
import com.azure.messaging.eventgrid.EventGridPublisherClient;
import com.azure.messaging.eventgrid.EventGridPublisherClientBuilder;
import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;
import com.ms.wmbanking.azure.common.spring.TutorialConfiguration;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Configuration
@Slf4j
@Import(TxnmanagerMessageHandler.class)
public class TxnManagerBeans {

    @Bean
    @Autowired
    public QueueClient approvalTopicClient(final TutorialConfiguration configuration) {
        val connStr = configuration.getAzure().getQueueConnectionString();
        if (isBlank(connStr)) {
            throw new IllegalArgumentException("Value missing for 'QueueConnectionString'.");
        }

        return new QueueClientBuilder().connectionString(connStr)
                                       .queueName("awaitingapproval")
                                       .buildClient();
    }

    @Bean
    @Autowired
    public EventGridPublisherClient executionEventGridClient(final TutorialConfiguration configuration) {
        val topicEndpoint = configuration.getAzure().getEventGridEndpoint();
        val key = configuration.getAzure().getEventGridKey();
        if (isBlank(topicEndpoint) || isBlank(key)) {
            throw new IllegalArgumentException("Event Grid Endpoint or Key is NULL");
        }
        return new EventGridPublisherClientBuilder()
                .endpoint(topicEndpoint)
                .credential(new AzureKeyCredential(key))
                .buildClient();
    }

    @Bean(name = {"txnmanagerInitiate","txnmanagerUpdate"})
    public TxnmanagerMessageHandler txnmanagerMessageHandler() {
        return new TxnmanagerMessageHandler();  //autowired
    }
}
