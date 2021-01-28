package com.ms.wmbanking.azure.common.spring;

import com.microsoft.azure.functions.ExecutionContext;
import com.ms.wmbanking.azure.common.logging.Slf4j2JavaUtilLoggingfBridge;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Base class to use for Azure functions that require Spring.  Make sure your global {@code Application} class
 * does a {@link @Import} of {@link ServerlessSpringBeans}.
 * <p>
 * Contract to use this:
 * <ul>
 *     <li>Single function method per class (comes from Spring Cloud way)</li>
 *     <li>Related bean (either by Function's name or specified name) must be either a {@link Function}, a {@link Consumer} or a {@link Supplier}</li>
 *     <li>All Functions in your FunctionApp share the same {@code Application} class</li>
 * </ul>
 * <p>
 * Supports creation from {@link @SpringBootTest} from test classes
 *
 * @param <I> Request type, or use {@link Void} if none (e.g. the invoked bean is a {@link Supplier})
 * @param <O> Response type, or use {@link Void} if the invoked bean returns {@code void}.
 */
@Slf4j
public abstract class AzureFunctionSpringHook<I, O> extends ServerlessSpringHook<I, O> {

    protected AzureFunctionSpringHook() {
        super();

    }

    protected AzureFunctionSpringHook(@NonNull final Class<?> clazz) {
        super(clazz);
    }

    protected O handleRequest(@NonNull final ExecutionContext executionContext) {
        return handleRequest(null, executionContext, executionContext.getFunctionName());
    }

    protected O handleRequest(final I request, @NonNull final ExecutionContext executionContext) {
        return handleRequest(request, executionContext, executionContext.getFunctionName());
    }

    protected O handleRequest(final I request,
                              @NonNull final ExecutionContext executionContext,
                              @NonNull final String springBeanName) {

        return super.handleRequest(request,
                                   new Slf4j2JavaUtilLoggingfBridge(executionContext.getLogger()),
                                   executionContext.getFunctionName());
    }

}
