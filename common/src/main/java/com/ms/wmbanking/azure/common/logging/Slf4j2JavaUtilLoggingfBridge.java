package com.ms.wmbanking.azure.common.logging;

import lombok.val;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Logger;


public class Slf4j2JavaUtilLoggingfBridge extends AbstractSlf4jBridge {

    private final Logger julLogger;

    public Slf4j2JavaUtilLoggingfBridge(Logger logger) {
        this.julLogger = logger;
    }

    @Override
    public String getName() {
        return julLogger.getName();
    }

    protected boolean isLoggable(Level level) {
        return julLogger.isLoggable(toJulLevel(level));
    }

    protected void log(Level level, FormattingTuple msgTuple) {
        julLogger.log(toJulLevel(level), msgTuple.getThrowable(), msgTuple::getMessage);
    }

    private java.util.logging.Level toJulLevel(final Level level) {
        switch (level) {
            case TRACE:
                return java.util.logging.Level.FINER;

            case DEBUG:
                return java.util.logging.Level.FINE;

            case INFO:
                return java.util.logging.Level.INFO;

            case WARN:
                return java.util.logging.Level.WARNING;

            case ERROR:
                return java.util.logging.Level.SEVERE;

            default:
                return null;
        }
    }
}
