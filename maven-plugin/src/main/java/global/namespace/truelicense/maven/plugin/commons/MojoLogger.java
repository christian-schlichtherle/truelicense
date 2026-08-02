/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.maven.plugin.commons;

import global.namespace.truelicense.build.tasks.commons.Logger;
import org.apache.maven.plugin.logging.Log;

/**
 * Adapts an underlying {@link Log} to the {@link Logger} interface.
 */
final class MojoLogger implements Logger {

    private final Log log;

    MojoLogger(final Log log) {
        this.log = log;
    }

    @Override
    public boolean isDebugEnabled() {
        return log.isDebugEnabled();
    }

    @Override
    public void debug(Throwable error) {
        log.debug(error);
    }

    @Override
    public void debug(CharSequence message) {
        log.debug(message);
    }

    @Override
    public void debug(CharSequence message, Throwable error) {
        log.debug(message, error);
    }

    @Override
    public boolean isInfoEnabled() {
        return log.isInfoEnabled();
    }

    @Override
    public void info(Throwable error) {
        log.info(error);
    }

    @Override
    public void info(CharSequence message) {
        log.info(message);
    }

    @Override
    public void info(CharSequence message, Throwable throwable) {
        log.info(message, throwable);
    }

    @Override
    public boolean isWarnEnabled() {
        return log.isWarnEnabled();
    }

    @Override
    public void warn(Throwable error) {
        log.warn(error);
    }

    @Override
    public void warn(CharSequence message) {
        log.warn(message);
    }

    @Override
    public void warn(CharSequence message, Throwable error) {
        log.warn(message, error);
    }

    @Override
    public boolean isErrorEnabled() {
        return log.isErrorEnabled();
    }

    @Override
    public void error(Throwable error) {
        log.error(error);
    }

    @Override
    public void error(CharSequence message) {
        log.error(message);
    }

    @Override
    public void error(CharSequence message, Throwable error) {
        log.error(message, error);
    }
}
