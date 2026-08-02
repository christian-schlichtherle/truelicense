/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.build.tasks.commons;

/**
 * A logger which prepends a prefix to every message.
 * The prefixed message is computed lazily, only if the severity level is enabled.
 */
public final class PrefixedLogger extends DecoratingLogger {

    private final String prefix;

    public PrefixedLogger(final Logger logger, final String prefix) {
        super(logger);
        this.prefix = prefix;
    }

    private String prefixedMessage(CharSequence message) {
        return prefix + message;
    }

    @Override
    public void debug(CharSequence message) {
        logger().debug(() -> prefixedMessage(message));
    }

    @Override
    public void debug(CharSequence message, Throwable error) {
        logger().debug(() -> prefixedMessage(message), error);
    }

    @Override
    public void info(CharSequence message) {
        logger().info(() -> prefixedMessage(message));
    }

    @Override
    public void info(CharSequence message, Throwable error) {
        logger().info(() -> prefixedMessage(message), error);
    }

    @Override
    public void warn(CharSequence message) {
        logger().warn(() -> prefixedMessage(message));
    }

    @Override
    public void warn(CharSequence message, Throwable error) {
        logger().warn(() -> prefixedMessage(message), error);
    }

    @Override
    public void error(CharSequence message) {
        logger().error(() -> prefixedMessage(message));
    }

    @Override
    public void error(CharSequence message, Throwable error) {
        logger().error(() -> prefixedMessage(message), error);
    }
}
