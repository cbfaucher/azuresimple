package com.ms.wmbanking.aws;

import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.ms.wmbanking.azure.common.logging.AbstractSlf4jBridge;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.slf4j.event.Level;
import org.slf4j.helpers.FormattingTuple;

import java.io.PrintWriter;
import java.io.StringWriter;

@RequiredArgsConstructor
public class Slf4j2AwsContextLoggerBridge extends AbstractSlf4jBridge {

    private final LambdaLogger lambdaLogger;

    @Override
    public String getName() {
        return lambdaLogger.toString();
    }

    @Override
    protected boolean isLoggable(Level level) {
        return true;
    }

    @Override
    protected void log(Level level, FormattingTuple msgTuple) {
        lambdaLogger.log(formatMessage(msgTuple));
    }

    private String formatMessage(FormattingTuple tuple) {
        val buffer = new StringBuilder(tuple.getMessage());
        if (tuple.getThrowable() != null) {
            val writer = new StringWriter();
            try (val printWriter = new PrintWriter(writer, true)) {
                tuple.getThrowable().printStackTrace(printWriter);
                buffer.append(writer.toString());
            }
        }
        return buffer.toString();
    }

}
