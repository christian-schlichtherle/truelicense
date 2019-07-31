/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.build.tasks.commons;

import java.util.function.Supplier;

/**
 * A generic logger interface with lazy message computation.
 */
public interface Logger {

    boolean isDebugEnabled();

    void debug(Throwable error);

    void debug(CharSequence message);

    void debug(CharSequence message, Throwable error);

    default void debug(final Supplier<CharSequence> message) {
        if (isDebugEnabled()) {
            debug(message.get());
        }
    }

    default void debug(final Supplier<CharSequence> message, final Throwable error) {
        if (isDebugEnabled()) {
            debug(message.get(), error);
        }
    }

    boolean isInfoEnabled();

    void info(Throwable error);

    void info(CharSequence message);

    void info(CharSequence message, Throwable error);

    default void info(final Supplier<CharSequence> message) {
        if (isInfoEnabled()) {
            info(message.get());
        }
    }

    default void info(final Supplier<CharSequence> message, final Throwable error) {
        if (isInfoEnabled()) {
            info(message.get(), error);
        }
    }

    boolean isWarnEnabled();

    void warn(Throwable error);

    void warn(CharSequence message);

    void warn(CharSequence message, Throwable error);

    default void warn(final Supplier<CharSequence> message) {
        if (isWarnEnabled()) {
            warn(message.get());
        }
    }

    default void warn(final Supplier<CharSequence> message, final Throwable error) {
        if (isWarnEnabled()) {
            warn(message.get(), error);
        }
    }

    boolean isErrorEnabled();

    void error(Throwable error);

    void error(CharSequence message);

    void error(CharSequence message, Throwable error);

    default void error(final Supplier<CharSequence> message) {
        if (isErrorEnabled()) {
            error(message.get());
        }
    }

    default void error(final Supplier<CharSequence> message, final Throwable error) {
        if (isErrorEnabled()) {
            error(message.get(), error);
        }
    }
}
