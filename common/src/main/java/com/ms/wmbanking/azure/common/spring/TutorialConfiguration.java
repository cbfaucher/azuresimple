package com.ms.wmbanking.azure.common.spring;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "tutorial")
@NoArgsConstructor
@Getter
public class TutorialConfiguration {

    private final AzureConfiguration azure = new AzureConfiguration();

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    static public class AzureConfiguration {
        private String queueConnectionString = null;
        private String eventGridEndpoint = null;
        private String eventGridKey = null;
    }
}