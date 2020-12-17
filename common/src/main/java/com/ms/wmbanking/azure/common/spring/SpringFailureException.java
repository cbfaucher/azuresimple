package com.ms.wmbanking.azure.common.spring;

/**
 * If things go south using {@link AzureFunctionSpringHook}
 */
public class SpringFailureException extends RuntimeException {
    public SpringFailureException(final String msg) {
        super(msg);
    }

    public SpringFailureException(Exception e) {
        super(e);
    }
}
