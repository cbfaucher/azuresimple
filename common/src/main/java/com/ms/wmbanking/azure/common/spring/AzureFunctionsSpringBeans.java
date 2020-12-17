package com.ms.wmbanking.azure.common.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Import this in your {@code Application} class
 *
 * @see AzureFunctionSpringHook
 */
@Configuration
public class AzureFunctionsSpringBeans {

    @Bean
    public ApplicationContextSingletonBean azureFunctionSpringHook() {
        return new ApplicationContextSingletonBean();
    }
}
