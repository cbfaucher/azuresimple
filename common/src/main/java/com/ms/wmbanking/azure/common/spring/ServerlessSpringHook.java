package com.ms.wmbanking.azure.common.spring;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Slf4j
public abstract class ServerlessSpringHook<I, O> {

    public ServerlessSpringHook() {
        SpringContextLoader.setSpringMainClassFromSystemPropertyIfNeeded();
    }

    public ServerlessSpringHook(final Class<?> clazz) {
        SpringContextLoader.setGlobalSpringMainClassName(clazz.getName());
    }

    protected O handleRequest(final I request,
                              @NonNull final String springBeanName) {
        return handleRequest(request, log, springBeanName);
    }

    protected O handleRequest(final I request,
                              final Logger logger,
                              @NonNull final String springBeanName) {

        val mylog = logger != null ? logger : log;

        //  make sure we have an ApplicationContext
        val appContext = fetchApplicationContext(mylog);

        try {
            //  get the bean
            mylog.info("Looking for Spring Bean: " + springBeanName);
            val object = appContext.getBean(springBeanName);

            //  execute
            if (object instanceof Function) {
                mylog.info("Bean is a java.util.Function.  Invoking it...");
                return (O) ((Function) object).apply(request);
            }

            if (object instanceof Supplier) {
                mylog.info("Bean is a java.util.Supplier.  Invoking it...");
                return (O) ((Supplier) object).get();
            }

            if (object instanceof Consumer) {
                mylog.info("Bean is a java.util.Consumer.  Invoking it...");
                ((Consumer) object).accept(request);
                return null;
            }

            throw new SpringFailureException(String.format("Bean <%s> is not a supported type (Function, Supplier, Consumer).  It is a %s",
                                                           springBeanName,
                                                           object.getClass().getName()));
        } catch (BeansException | ClassCastException e) {
            throw new SpringFailureException(e);
        }
    }

    private ApplicationContext fetchApplicationContext(final Logger log) {

        log.info(String.format("Querying ApplicationContextSingletonBean<%s> for ApplicationContext...", ApplicationContextSingletonBean.class.hashCode()));

        val appContext = ApplicationContextSingletonBean.getApplicationContext();
        if (appContext != null) {
            log.info(String.format("Got AppContext ID=%s.  Returning it...", appContext.getId()));
            return appContext;
        }

        log.info("No AppContext loaded!  Requesting a new one...");
        return SpringContextLoader.getApplicationContext();
    }
}
