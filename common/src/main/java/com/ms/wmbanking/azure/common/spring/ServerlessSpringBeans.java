package com.ms.wmbanking.azure.common.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Import this in your {@code Application} class
 *
 * @see AzureFunctionSpringHook
 */
@Configuration
public class ServerlessSpringBeans {

    @Bean
    public ApplicationContextSingletonBean serverlessApplicationContextHolder() {
        return new ApplicationContextSingletonBean();
    }
}
