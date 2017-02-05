/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.maven.plugin;

import javax.annotation.Nullable;
import org.apache.maven.plugin.logging.Log;

/**
 * @author Christian Schlichtherle
 */
class DecoratingLog implements Log {

    /** The nullable log. */
    @Nullable Log log;

    DecoratingLog() { }

    DecoratingLog(final @Nullable Log log) {
        this.log = log;
    }

    @Override
    public boolean isDebugEnabled() { return log.isDebugEnabled(); }

    @Override
    public void debug(CharSequence content) { log.debug(content); }

    @Override
    public void debug(CharSequence content, Throwable error) {
        log.debug(content, error);
    }

    @Override
    public void debug(Throwable error) { log.debug(error); }

    @Override
    public boolean isInfoEnabled() { return log.isInfoEnabled(); }

    @Override
    public void info(CharSequence content) { log.info(content); }

    @Override
    public void info(CharSequence content, Throwable error) {
        log.info(content, error);
    }

    @Override
    public void info(Throwable error) { log.info(error); }

    @Override
    public boolean isWarnEnabled() { return log.isWarnEnabled(); }

    @Override
    public void warn(CharSequence content) { log.warn(content); }

    @Override
    public void warn(CharSequence content, Throwable error) {
        log.warn(content, error);
    }

    @Override
    public void warn(Throwable error) { log.warn(error); }

    @Override
    public boolean isErrorEnabled() { return log.isErrorEnabled(); }

    @Override
    public void error(CharSequence content) { log.error(content); }

    @Override
    public void error(CharSequence content, Throwable error) {
        log.error(content, error);
    }

    @Override
    public void error(Throwable error) { log.error(error); }
}
