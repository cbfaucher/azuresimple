package com.ms.wmbanking.azure.common.spring;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PreDestroy;
import java.util.Date;

/**
 * Internal singleton (yes, I know) bean that keeps track of active {@link ApplicationContext}.
 */
@NoArgsConstructor
@Slf4j
public class ApplicationContextSingletonBean implements ApplicationContextAware {

    private static final Object lock = new Object();

    @Getter
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(final ApplicationContext newAppContext) throws BeansException {

        synchronized (lock) {
            if (applicationContext != null && applicationContext != newAppContext) {
                throw new IllegalArgumentException(String.format("ApplicationContext Singleton already attached to another ApplicationContext (Actual=%s @ %s / New=%s @ %s)",
                                                                 applicationContext.getId(), new Date(applicationContext.getStartupDate()).toString(),
                                                                 newAppContext.getId(), new Date(applicationContext.getStartupDate()).toString()));
            }

            log.info(String.format("ApplicationContext Singleton attached to AppContext ID=%s", newAppContext.getId()));
            applicationContext = newAppContext;
        }
    }

    @PreDestroy
    public void clearApplicationContext() {
        synchronized (lock) {
            if (applicationContext != null) {
                log.info("Clearing Application Context ID #" + applicationContext.getId());
            }
            applicationContext = null;
        }
    }
}
