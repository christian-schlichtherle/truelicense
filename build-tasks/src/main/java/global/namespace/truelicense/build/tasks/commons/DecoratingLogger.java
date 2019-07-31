/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.build.tasks.commons;

import global.namespace.neuron.di.java.Caching;
import global.namespace.neuron.di.java.Neuron;

@Neuron
public interface DecoratingLogger extends Logger {

    /**
     * Returns the underlying logger.
     */
    Logger logger();

    @Override
    @Caching
    default boolean isDebugEnabled() {
        return logger().isDebugEnabled();
    }

    @Override
    default void debug(Throwable error) {
        logger().debug(error);
    }

    @Override
    default void debug(CharSequence message) {
        logger().debug(message);
    }

    @Override
    default void debug(CharSequence message, Throwable error) {
        logger().debug(message, error);
    }

    @Override
    @Caching
    default boolean isInfoEnabled() {
        return logger().isInfoEnabled();
    }

    @Override
    default void info(Throwable error) {
        logger().info(error);
    }

    @Override
    default void info(CharSequence message) {
        logger().info(message);
    }

    @Override
    default void info(CharSequence message, Throwable error) {
        logger().info(message, error);
    }

    @Override
    @Caching
    default boolean isWarnEnabled() {
        return logger().isWarnEnabled();
    }

    @Override
    default void warn(Throwable error) {
        logger().warn(error);
    }

    @Override
    default void warn(CharSequence message) {
        logger().warn(message);
    }

    @Override
    default void warn(CharSequence message, Throwable error) {
        logger().warn(message, error);
    }

    @Override
    @Caching
    default boolean isErrorEnabled() {
        return logger().isErrorEnabled();
    }

    @Override
    default void error(Throwable error) {
        logger().error(error);
    }

    @Override
    default void error(CharSequence message) {
        logger().error(message);
    }

    @Override
    default void error(CharSequence message, Throwable error) {
        logger().error(message, error);
    }
}
