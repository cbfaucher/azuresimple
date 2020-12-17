package com.ms.wmbanking.azure.common.spring;

import com.microsoft.azure.functions.ExecutionContext;
import lombok.NonNull;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Base class to use for Azure functions that require Spring.  Make sure your global {@code Application} class
 * does a {@link @Import} of {@link AzureFunctionsSpringBeans}.
 *
 * Contract to use this:
 * <ul>
 *     <li>Single function method per class (comes from Spring Cloud way)</li>
 *     <li>Related bean (either by Function's name or specified name) must be either a {@link Function}, a {@link Consumer} or a {@link Supplier}</li>
 *     <li>All Functions in your FunctionApp share the same {@code Application} class</li>
 * </ul>
 *
 * Supports creation from {@link @SpringBootTest} from test classes
 *
 *
 * @param <I> Request type, or use {@link Void} if none (e.g. the invoked bean is a {@link Supplier})
 * @param <O> Response type, or use {@link Void} if the invoked bean returns {@code void}.
 */
public abstract class AzureFunctionSpringHook<I, O> {

    protected O handleRequest(@NonNull final ExecutionContext executionContext) {
        return handleRequest(null, executionContext, executionContext.getFunctionName());
    }

    protected O handleRequest(final I request, @NonNull final ExecutionContext executionContext) {
        return handleRequest(request, executionContext, executionContext.getFunctionName());
    }

    protected O handleRequest(final I request, @NonNull final ExecutionContext executionContext,
                              @NonNull final String springBeanName) {

        //  make sure we have an ApplicationContext
        val appContext = Optional.ofNullable(ApplicationContextSingletonBean.getApplicationContext())
                                 .orElseGet(this::loadApplicationContext);

        try {
            //  get the bean
            executionContext.getLogger().info("Looking for Spring Bean: " + springBeanName);
            val object = appContext.getBean(springBeanName);

            //  execute
            if (object instanceof Function) {
                executionContext.getLogger().info("Bean is a java.util.Function.  Invoking it...");
                return (O) ((Function) object).apply(request);
            }

            if (object instanceof Supplier) {
                executionContext.getLogger().info("Bean is a java.util.Supplier.  Invoking it...");
                return (O) ((Supplier) object).get();
            }

            if (object instanceof Consumer) {
                executionContext.getLogger().info("Bean is a java.util.Consumer.  Invoking it...");
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

    private ConfigurableApplicationContext loadApplicationContext() {
        val springMainClassName = System.getProperty("MAIN_CLASS");
        if (StringUtils.isBlank(springMainClassName)) {
            throw new SpringFailureException("Spring's context main class name missing (MAIN_CLASS=...)");
        }

        try {
            val springMainClass = Class.forName(springMainClassName);

            return SpringApplication.run(springMainClass);
        } catch (Exception e) {
            throw new SpringFailureException(e);
        }
    }
}
