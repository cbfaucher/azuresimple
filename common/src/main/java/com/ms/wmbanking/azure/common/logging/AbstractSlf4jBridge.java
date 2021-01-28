package com.ms.wmbanking.azure.common.logging;

import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MessageFormatter;

public abstract class AbstractSlf4jBridge implements org.slf4j.Logger {

    /**
     *
     * @return Logger name
     */
    @Override
    public abstract String getName();

    protected abstract boolean isLoggable(Level level);

    protected abstract void log(Level level, FormattingTuple msgTuple);

    @Override
    public boolean isTraceEnabled() {
        return isLoggable(Level.TRACE);
    }

    @Override
    public void trace(String s) {
        trace(s, null, null);
    }

    @Override
    public void trace(String s, Object o) {
        trace(s, o, null);
    }

    @Override
    public void trace(String s, Object o, Object o1) {
        trace(s, new Object[]{o, o1});
    }

    @Override
    public void trace(String s, Object... objects) {
        log(Level.TRACE, MessageFormatter.arrayFormat(s, objects));
    }

    @Override
    public void trace(String s, Throwable throwable) {
        trace(s, new Object[]{throwable});
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return isTraceEnabled();
    }

    @Override
    public void trace(Marker marker, String s) {
        trace(s, null, null);
    }

    @Override
    public void trace(Marker marker, String s, Object o) {
        trace(s, o, null);
    }

    @Override
    public void trace(Marker marker, String s, Object o, Object o1) {
        trace(s, o, o1);
    }

    @Override
    public void trace(Marker marker, String s, Object... objects) {
        trace(s, objects);
    }

    @Override
    public void trace(Marker marker, String s, Throwable throwable) {
        trace(s, throwable);
    }

    @Override
    public boolean isDebugEnabled() {
        return isLoggable(Level.DEBUG);
    }

    @Override
    public void debug(String s) {
        debug(s, new Object[0]);
    }

    @Override
    public void debug(String s, Object o) {
        debug(s, new Object[]{o});
    }

    @Override
    public void debug(String s, Object o, Object o1) {
        debug(s, new Object[]{o, o1});
    }

    @Override
    public void debug(String s, Object... objects) {
        log(Level.DEBUG, MessageFormatter.arrayFormat(s, objects));
    }

    @Override
    public void debug(String s, Throwable throwable) {
        debug(s, new Object[]{throwable});
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return isDebugEnabled();
    }

    @Override
    public void debug(Marker marker, String s) {
        debug(s);
    }

    @Override
    public void debug(Marker marker, String s, Object o) {
        debug(s, o);
    }

    @Override
    public void debug(Marker marker, String s, Object o, Object o1) {
        debug(s, o, o1);
    }

    @Override
    public void debug(Marker marker, String s, Object... objects) {
        debug(s, objects);
    }

    @Override
    public void debug(Marker marker, String s, Throwable throwable) {
        debug(s, throwable);
    }

    @Override
    public boolean isInfoEnabled() {
        return isLoggable(Level.INFO);
    }

    @Override
    public void info(String s) {
        info(s, new Object[0]);
    }

    @Override
    public void info(String s, Object o) {
        info(s, new Object[]{o});
    }

    @Override
    public void info(String s, Object o, Object o1) {
        info(s, new Object[]{o, o1});
    }

    @Override
    public void info(String s, Object... objects) {
        log(Level.INFO, MessageFormatter.arrayFormat(s, objects));
    }

    @Override
    public void info(String s, Throwable throwable) {
        info(s, new Object[]{throwable});
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return isInfoEnabled();
    }

    @Override
    public void info(Marker marker, String s) {
        info(s);
    }

    @Override
    public void info(Marker marker, String s, Object o) {
        info(s, o);
    }

    @Override
    public void info(Marker marker, String s, Object o, Object o1) {
        info(s, o, o1);
    }

    @Override
    public void info(Marker marker, String s, Object... objects) {
        info(s, objects);
    }

    @Override
    public void info(Marker marker, String s, Throwable throwable) {
        info(s, throwable);
    }

    @Override
    public boolean isWarnEnabled() {
        return isLoggable(Level.WARN);
    }

    @Override
    public void warn(String s) {
        warn(s, new Object[0]);
    }

    @Override
    public void warn(String s, Object o) {
        warn(s, new Object[]{0});
    }

    @Override
    public void warn(String s, Object... objects) {
        log(Level.WARN, MessageFormatter.arrayFormat(s, objects));
    }

    @Override
    public void warn(String s, Object o, Object o1) {
        warn(s, new Object[]{o, o1});
    }

    @Override
    public void warn(String s, Throwable throwable) {
        warn(s, new Object[]{throwable});
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return isWarnEnabled();
    }

    @Override
    public void warn(Marker marker, String s) {
        warn(s, new Object[0]);
    }

    @Override
    public void warn(Marker marker, String s, Object o) {
        warn(s, new Object[]{o});
    }

    @Override
    public void warn(Marker marker, String s, Object o, Object o1) {
        warn(s, new Object[]{o, o1});
    }

    @Override
    public void warn(Marker marker, String s, Object... objects) {
        warn(s, objects);
    }

    @Override
    public void warn(Marker marker, String s, Throwable throwable) {
        warn(s, new Object[]{throwable});
    }

    @Override
    public boolean isErrorEnabled() {
        return isLoggable(Level.ERROR);
    }

    @Override
    public void error(String s) {
        error(s, new Object[0]);
    }

    @Override
    public void error(String s, Object o) {
        error(s, new Object[]{o});
    }

    @Override
    public void error(String s, Object o, Object o1) {
        error(s, new Object[]{o, o1});
    }

    @Override
    public void error(String s, Object... objects) {
        log(Level.ERROR, MessageFormatter.arrayFormat(s, objects));
    }

    @Override
    public void error(String s, Throwable throwable) {
        error(s, new Object[]{throwable});
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return isErrorEnabled();
    }

    @Override
    public void error(Marker marker, String s) {
        error(s);
    }

    @Override
    public void error(Marker marker, String s, Object o) {
        error(s, o);
    }

    @Override
    public void error(Marker marker, String s, Object o, Object o1) {
        error(s, o, o1);
    }

    @Override
    public void error(Marker marker, String s, Object... objects) {
        error(s, objects);
    }

    @Override
    public void error(Marker marker, String s, Throwable throwable) {
        error(s, throwable);
    }

}
