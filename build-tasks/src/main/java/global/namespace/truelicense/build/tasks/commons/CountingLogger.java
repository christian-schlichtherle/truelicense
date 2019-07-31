/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.build.tasks.commons;

import global.namespace.neuron.di.java.Caching;
import global.namespace.neuron.di.java.Neuron;

import java.util.concurrent.atomic.AtomicLong;

@Neuron
public interface CountingLogger extends DecoratingLogger {

    @Caching
    default AtomicLong debugCounter() {
        return new AtomicLong();
    }

    @Override
    default void debug(final Throwable error) {
        debugCounter().incrementAndGet();
        logger().debug(error);
    }

    @Override
    default void debug(final CharSequence message) {
        debugCounter().incrementAndGet();
        logger().debug(message);
    }

    @Override
    default void debug(final CharSequence message, final Throwable error) {
        debugCounter().incrementAndGet();
        logger().debug(message, error);
    }

    @Caching
    default AtomicLong infoCounter() {
        return new AtomicLong();
    }

    @Override
    default void info(final Throwable error) {
        infoCounter().incrementAndGet();
        logger().info(error);
    }

    @Override
    default void info(final CharSequence message) {
        infoCounter().incrementAndGet();
        logger().info(message);
    }

    @Override
    default void info(final CharSequence message, final Throwable error) {
        infoCounter().incrementAndGet();
        logger().info(message, error);
    }

    @Caching
    default AtomicLong warnCounter() {
        return new AtomicLong();
    }

    @Override
    default void warn(final Throwable error) {
        warnCounter().incrementAndGet();
        logger().warn(error);
    }

    @Override
    default void warn(final CharSequence message) {
        warnCounter().incrementAndGet();
        logger().warn(message);
    }

    @Override
    default void warn(final CharSequence message, final Throwable error) {
        warnCounter().incrementAndGet();
        logger().warn(message, error);
    }

    @Caching
    default AtomicLong errorCounter() {
        return new AtomicLong();
    }

    @Override
    default void error(final Throwable error) {
        errorCounter().incrementAndGet();
        logger().error(error);
    }

    @Override
    default void error(final CharSequence message) {
        errorCounter().incrementAndGet();
        logger().error(message);
    }

    @Override
    default void error(final CharSequence message, final Throwable error) {
        errorCounter().incrementAndGet();
        logger().error(message, error);
    }
}
