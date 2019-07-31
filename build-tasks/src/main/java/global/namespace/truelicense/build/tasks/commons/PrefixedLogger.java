/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.build.tasks.commons;

import global.namespace.neuron.di.java.Neuron;

@Neuron
public interface PrefixedLogger extends DecoratingLogger {

    /**
     * Returns the message prefix.
     */
    String prefix();

    default String prefixedMessage(CharSequence message) {
        return prefix() + message;
    }

    @Override
    default void debug(CharSequence message) {
        logger().debug(() -> prefixedMessage(message));
    }

    @Override
    default void debug(CharSequence message, Throwable error) {
        logger().debug(() -> prefixedMessage(message), error);
    }

    @Override
    default void info(CharSequence message) {
        logger().info(() -> prefixedMessage(message));
    }

    @Override
    default void info(CharSequence message, Throwable error) {
        logger().info(() -> prefixedMessage(message), error);
    }

    @Override
    default void warn(CharSequence message) {
        logger().warn(() -> prefixedMessage(message));
    }

    @Override
    default void warn(CharSequence message, Throwable error) {
        logger().warn(() -> prefixedMessage(message), error);
    }

    @Override
    default void error(CharSequence message) {
        logger().error(() -> prefixedMessage(message));
    }

    @Override
    default void error(CharSequence message, Throwable error) {
        logger().error(() -> prefixedMessage(message), error);
    }
}
