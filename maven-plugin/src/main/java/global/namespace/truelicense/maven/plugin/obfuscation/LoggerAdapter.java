/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package global.namespace.truelicense.maven.plugin.obfuscation;

import org.apache.maven.plugin.logging.Log;
import org.slf4j.helpers.MarkerIgnoringBase;

import static org.slf4j.helpers.MessageFormatter.arrayFormat;
import static org.slf4j.helpers.MessageFormatter.format;

/**
 * @author Christian Schlichtherle
 */
final class LoggerAdapter extends MarkerIgnoringBase {

    private final Log log;

    LoggerAdapter(final Log log) { this.log = log; }

    @Override
    public String getName() { throw new UnsupportedOperationException(); }

    @Override
    public boolean isTraceEnabled() { return false; }

    @Override
    public void trace(String msg) { }

    @Override
    public void trace(String format, Object arg) { }

    @Override
    public void trace(String format, Object arg1, Object arg2) { }

    @Override
    public void trace(String format, Object... arguments) { }

    @Override
    public void trace(String msg, Throwable t) { }

    @Override
    public boolean isDebugEnabled() { return log.isDebugEnabled(); }

    @Override
    public void debug(String msg) { log.debug(msg); }

    @Override
    public void debug(String format, Object arg) {
        if (log.isDebugEnabled())
            log.debug(format(format, arg).getMessage());
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if (log.isDebugEnabled())
            log.debug(format(format, arg1, arg2).getMessage());
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (log.isDebugEnabled())
            log.debug(arrayFormat(format, arguments).getMessage());
    }

    @Override
    public void debug(String msg, Throwable t) { log.debug(msg, t); }

    @Override
    public boolean isInfoEnabled() { return log.isInfoEnabled(); }

    @Override
    public void info(String msg) { log.info(msg); }

    @Override
    public void info(String format, Object arg) {
        if (log.isInfoEnabled())
            log.info(format(format, arg).getMessage());
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        if (log.isInfoEnabled())
            log.info(format(format, arg1, arg2).getMessage());
    }

    @Override
    public void info(String format, Object... arguments) {
        if (log.isInfoEnabled())
            log.info(arrayFormat(format, arguments).getMessage());
    }

    @Override
    public void info(String msg, Throwable t) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWarnEnabled() { return log.isWarnEnabled(); }

    @Override
    public void warn(String msg) { log.warn(msg); }

    @Override
    public void warn(String format, Object arg) {
        if (log.isWarnEnabled())
            log.warn(format(format, arg).getMessage());
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        if (log.isWarnEnabled())
            log.warn(format(format, arg1, arg2).getMessage());
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (log.isWarnEnabled())
            log.warn(arrayFormat(format, arguments).getMessage());
    }

    @Override
    public void warn(String msg, Throwable t) { log.warn(msg, t); }

    @Override
    public boolean isErrorEnabled() { return log.isErrorEnabled(); }

    @Override
    public void error(String msg) { log.error(msg); }

    @Override
    public void error(String format, Object arg) {
        if (log.isErrorEnabled())
            log.error(format(format, arg).getMessage());
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        if (log.isErrorEnabled())
            log.error(format(format, arg1, arg2).getMessage());
    }

    @Override
    public void error(String format, Object... arguments) {
        if (log.isErrorEnabled())
            log.error(arrayFormat(format, arguments).getMessage());
    }

    @Override
    public void error(String msg, Throwable t) { log.error(msg, t); }
}
