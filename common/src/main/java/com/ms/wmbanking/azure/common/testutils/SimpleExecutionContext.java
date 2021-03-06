package com.ms.wmbanking.azure.common.testutils;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;

import java.util.UUID;
import java.util.logging.Logger;

/**
 * An utility implementation for {@link ExecutionContext}
 */
public class SimpleExecutionContext implements ExecutionContext {

    static public ExecutionContext createFrom(final Class<?> fctClass,
                                              final String methodName,
                                              final Class<?> ... paramClasses) {
        return createFrom(UUID.randomUUID().toString(), fctClass, methodName, paramClasses);
    }

    @SneakyThrows
    static public ExecutionContext createFrom(final String invocationId,
                                              final Class<?> fctClass,
                                              final String methodName,
                                              final Class<?> ... paramTypes) {
        val method = fctClass.getDeclaredMethod(methodName, paramTypes);
        val fctName = method.getAnnotation(FunctionName.class).value();

        return new SimpleExecutionContext(invocationId, fctName);
    }

    @Getter
    final private String invocationId;

    @Getter
    final private String functionName;

    public SimpleExecutionContext(String functionName) {
        this(UUID.randomUUID().toString(), functionName);
    }

    public SimpleExecutionContext(String invocationId, String functionName) {
        this.invocationId = invocationId;
        this.functionName = functionName;
    }

    @Override
    public Logger getLogger() {
        return Logger.getLogger(functionName);
    }
}
