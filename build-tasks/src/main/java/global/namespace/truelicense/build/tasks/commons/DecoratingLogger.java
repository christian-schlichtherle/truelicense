/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.build.tasks.commons;

/**
 * A logger which delegates to an underlying logger.
 */
public abstract class DecoratingLogger implements Logger {

    private final Logger logger;

    protected DecoratingLogger(final Logger logger) {
        this.logger = logger;
    }

    /**
     * Returns the underlying logger.
     */
    public final Logger logger() {
        return logger;
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public void debug(Throwable error) {
        logger.debug(error);
    }

    @Override
    public void debug(CharSequence message) {
        logger.debug(message);
    }

    @Override
    public void debug(CharSequence message, Throwable error) {
        logger.debug(message, error);
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public void info(Throwable error) {
        logger.info(error);
    }

    @Override
    public void info(CharSequence message) {
        logger.info(message);
    }

    @Override
    public void info(CharSequence message, Throwable error) {
        logger.info(message, error);
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarnEnabled();
    }

    @Override
    public void warn(Throwable error) {
        logger.warn(error);
    }

    @Override
    public void warn(CharSequence message) {
        logger.warn(message);
    }

    @Override
    public void warn(CharSequence message, Throwable error) {
        logger.warn(message, error);
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public void error(Throwable error) {
        logger.error(error);
    }

    @Override
    public void error(CharSequence message) {
        logger.error(message);
    }

    @Override
    public void error(CharSequence message, Throwable error) {
        logger.error(message, error);
    }
}
