/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package global.namespace.truelicense.maven.plugin.obfuscation.core;

import org.slf4j.Logger;
import org.slf4j.Marker;

/**
 * @author Christian Schlichtherle
 */
final class SubjectLogger implements Logger {

    private final Logger logger;
    private final String subject;

    SubjectLogger(final Logger logger, final String subject) {
        this.logger = logger;
        this.subject = subject;
    }

    private String prefixed(String message) { return subject + ": " + message; }

    @Override
    public String getName() { return logger.getName(); }

    @Override
    public boolean isTraceEnabled() { return logger.isTraceEnabled(); }

    @Override
    public boolean isDebugEnabled() { return logger.isDebugEnabled(); }

    @Override
    public boolean isInfoEnabled() { return logger.isInfoEnabled(); }

    @Override
    public boolean isWarnEnabled() { return logger.isWarnEnabled(); }

    @Override
    public boolean isErrorEnabled() { return logger.isErrorEnabled(); }

    @Override
    public void trace(String msg) {
        if (logger.isTraceEnabled()) logger.trace(prefixed(msg));
    }

    @Override
    public void debug(String msg) {
        if (logger.isDebugEnabled()) logger.debug(prefixed(msg));
    }

    @Override
    public void info(String msg) {
        if (logger.isInfoEnabled()) logger.info(prefixed(msg));
    }

    @Override
    public void warn(String msg) {
        if (logger.isWarnEnabled()) logger.warn(prefixed(msg));
    }

    @Override
    public void error(String msg) {
        if (logger.isErrorEnabled()) logger.error(prefixed(msg));
    }

    @Override
    public void trace(String format, Object arg) {
        if (logger.isTraceEnabled()) logger.trace(prefixed(format), arg);
    }

    @Override
    public void debug(String format, Object arg) {
        if (logger.isDebugEnabled()) logger.debug(prefixed(format), arg);
    }

    @Override
    public void info(String format, Object arg) {
        if (logger.isInfoEnabled()) logger.info(prefixed(format), arg);
    }

    @Override
    public void warn(String format, Object arg) {
        if (logger.isWarnEnabled()) logger.warn(prefixed(format), arg);
    }

    @Override
    public void error(String format, Object arg) {
        if (logger.isErrorEnabled()) logger.error(prefixed(format), arg);
    }

    @Override
    public void trace(String format, Object arg1, Object arg2) {
        if (logger.isTraceEnabled()) logger.trace(prefixed(format), arg1, arg2);
    }

    @Override
    public void debug(String format, Object arg1, Object arg2) {
        if (logger.isDebugEnabled()) logger.debug(prefixed(format), arg1, arg2);
    }

    @Override
    public void info(String format, Object arg1, Object arg2) {
        if (logger.isInfoEnabled()) logger.info(prefixed(format), arg1, arg2);
    }

    @Override
    public void warn(String format, Object arg1, Object arg2) {
        if (logger.isWarnEnabled()) logger.warn(prefixed(format), arg1, arg2);
    }

    @Override
    public void error(String format, Object arg1, Object arg2) {
        if (logger.isErrorEnabled()) logger.error(prefixed(format), arg1, arg2);
    }

    @Override
    public void trace(String format, Object... arguments) {
        if (logger.isTraceEnabled()) logger.trace(prefixed(format), arguments);
    }

    @Override
    public void debug(String format, Object... arguments) {
        if (logger.isDebugEnabled()) logger.debug(prefixed(format), arguments);
    }

    @Override
    public void info(String format, Object... arguments) {
        if (logger.isInfoEnabled()) logger.info(prefixed(format), arguments);
    }

    @Override
    public void warn(String format, Object... arguments) {
        if (logger.isWarnEnabled()) logger.warn(prefixed(format), arguments);
    }

    @Override
    public void error(String format, Object... arguments) {
        if (logger.isErrorEnabled()) logger.error(prefixed(format), arguments);
    }

    @Override
    public void trace(String msg, Throwable t) {
        if (logger.isTraceEnabled()) logger.trace(prefixed(msg), t);
    }

    @Override
    public void debug(String msg, Throwable t) {
        if (logger.isDebugEnabled()) logger.debug(prefixed(msg), t);
    }

    @Override
    public void info(String msg, Throwable t) {
        if (logger.isInfoEnabled()) logger.info(prefixed(msg), t);
    }

    @Override
    public void warn(String msg, Throwable t) {
        if (logger.isWarnEnabled()) logger.warn(prefixed(msg), t);
    }

    @Override
    public void error(String msg, Throwable t) {
        if (logger.isErrorEnabled()) logger.error(prefixed(msg), t);
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return logger.isTraceEnabled(marker);
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return logger.isDebugEnabled(marker);
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return logger.isInfoEnabled(marker);
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return logger.isWarnEnabled(marker);
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return logger.isErrorEnabled(marker);
    }

    @Override
    public void trace(Marker marker, String msg) {
        if (logger.isTraceEnabled(marker)) logger.trace(prefixed(msg));
    }

    @Override
    public void debug(Marker marker, String msg) {
        if (logger.isDebugEnabled(marker)) logger.debug(prefixed(msg));
    }

    @Override
    public void info(Marker marker, String msg) {
        if (logger.isInfoEnabled(marker)) logger.info(prefixed(msg));
    }

    @Override
    public void warn(Marker marker, String msg) {
        if (logger.isWarnEnabled(marker)) logger.warn(prefixed(msg));
    }

    @Override
    public void error(Marker marker, String msg) {
        if (logger.isErrorEnabled(marker)) logger.error(prefixed(msg));
    }

    @Override
    public void trace(Marker marker, String format, Object arg) {
        if (logger.isTraceEnabled(marker)) logger.trace(prefixed(format), arg);
    }

    @Override
    public void debug(Marker marker, String format, Object arg) {
        if (logger.isDebugEnabled(marker)) logger.debug(prefixed(format), arg);
    }

    @Override
    public void info(Marker marker, String format, Object arg) {
        if (logger.isInfoEnabled(marker)) logger.info(prefixed(format), arg);
    }

    @Override
    public void warn(Marker marker, String format, Object arg) {
        if (logger.isWarnEnabled(marker)) logger.warn(prefixed(format), arg);
    }

    @Override
    public void error(Marker marker, String format, Object arg) {
        if (logger.isErrorEnabled(marker)) logger.error(prefixed(format), arg);
    }

    @Override
    public void trace(Marker marker, String format, Object arg1, Object arg2) {
        if (logger.isTraceEnabled(marker)) logger.trace(prefixed(format), arg1, arg2);
    }

    @Override
    public void debug(Marker marker, String format, Object arg1, Object arg2) {
        if (logger.isDebugEnabled(marker)) logger.debug(prefixed(format), arg1, arg2);
    }

    @Override
    public void info(Marker marker, String format, Object arg1, Object arg2) {
        if (logger.isInfoEnabled(marker)) logger.info(prefixed(format), arg1, arg2);
    }

    @Override
    public void warn(Marker marker, String format, Object arg1, Object arg2) {
        if (logger.isWarnEnabled(marker)) logger.warn(prefixed(format), arg1, arg2);
    }

    @Override
    public void error(Marker marker, String format, Object arg1, Object arg2) {
        if (logger.isErrorEnabled(marker)) logger.error(prefixed(format), arg1, arg2);
    }

    @Override
    public void trace(Marker marker, String format, Object... arguments) {
        if (logger.isTraceEnabled(marker)) logger.trace(prefixed(format), arguments);
    }

    @Override
    public void debug(Marker marker, String format, Object... arguments) {
        if (logger.isDebugEnabled(marker)) logger.debug(prefixed(format), arguments);
    }

    @Override
    public void info(Marker marker, String format, Object... arguments) {
        if (logger.isInfoEnabled(marker)) logger.info(prefixed(format), arguments);
    }

    @Override
    public void warn(Marker marker, String format, Object... arguments) {
        if (logger.isWarnEnabled(marker)) logger.warn(prefixed(format), arguments);
    }

    @Override
    public void error(Marker marker, String format, Object... arguments) {
        if (logger.isErrorEnabled(marker)) logger.error(prefixed(format), arguments);
    }

    @Override
    public void trace(Marker marker, String msg, Throwable t) {
        if (logger.isTraceEnabled(marker)) logger.trace(prefixed(msg), t);
    }

    @Override
    public void debug(Marker marker, String msg, Throwable t) {
        if (logger.isDebugEnabled(marker)) logger.debug(prefixed(msg), t);
    }

    @Override
    public void info(Marker marker, String msg, Throwable t) {
        if (logger.isInfoEnabled(marker)) logger.info(prefixed(msg), t);
    }

    @Override
    public void warn(Marker marker, String msg, Throwable t) {
        if (logger.isWarnEnabled(marker)) logger.warn(prefixed(msg), t);
    }

    @Override
    public void error(Marker marker, String msg, Throwable t) {
        if (logger.isErrorEnabled(marker)) logger.error(prefixed(msg), t);
    }
}
