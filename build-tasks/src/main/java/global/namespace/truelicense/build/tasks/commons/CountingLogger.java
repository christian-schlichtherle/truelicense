/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.build.tasks.commons;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A logger which counts the calls per severity level.
 * The error count decides whether a task fails after its run, so counts must persist for the life of this logger.
 */
public final class CountingLogger extends DecoratingLogger {

    private final AtomicLong debugCounter = new AtomicLong();
    private final AtomicLong infoCounter = new AtomicLong();
    private final AtomicLong warnCounter = new AtomicLong();
    private final AtomicLong errorCounter = new AtomicLong();

    public CountingLogger(Logger logger) {
        super(logger);
    }

    public AtomicLong debugCounter() {
        return debugCounter;
    }

    @Override
    public void debug(Throwable error) {
        debugCounter.incrementAndGet();
        super.debug(error);
    }

    @Override
    public void debug(CharSequence message) {
        debugCounter.incrementAndGet();
        super.debug(message);
    }

    @Override
    public void debug(CharSequence message, Throwable error) {
        debugCounter.incrementAndGet();
        super.debug(message, error);
    }

    public AtomicLong infoCounter() {
        return infoCounter;
    }

    @Override
    public void info(Throwable error) {
        infoCounter.incrementAndGet();
        super.info(error);
    }

    @Override
    public void info(CharSequence message) {
        infoCounter.incrementAndGet();
        super.info(message);
    }

    @Override
    public void info(CharSequence message, Throwable error) {
        infoCounter.incrementAndGet();
        super.info(message, error);
    }

    public AtomicLong warnCounter() {
        return warnCounter;
    }

    @Override
    public void warn(Throwable error) {
        warnCounter.incrementAndGet();
        super.warn(error);
    }

    @Override
    public void warn(CharSequence message) {
        warnCounter.incrementAndGet();
        super.warn(message);
    }

    @Override
    public void warn(CharSequence message, Throwable error) {
        warnCounter.incrementAndGet();
        super.warn(message, error);
    }

    public AtomicLong errorCounter() {
        return errorCounter;
    }

    @Override
    public void error(Throwable error) {
        errorCounter.incrementAndGet();
        super.error(error);
    }

    @Override
    public void error(CharSequence message) {
        errorCounter.incrementAndGet();
        super.error(message);
    }

    @Override
    public void error(CharSequence message, Throwable error) {
        errorCounter.incrementAndGet();
        super.error(message, error);
    }
}
