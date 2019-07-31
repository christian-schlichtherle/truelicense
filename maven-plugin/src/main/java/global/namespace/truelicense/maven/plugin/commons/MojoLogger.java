/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.maven.plugin.commons;

import global.namespace.neuron.di.java.Caching;
import global.namespace.neuron.di.java.Neuron;
import global.namespace.truelicense.build.tasks.commons.Logger;
import org.apache.maven.plugin.logging.Log;

import static global.namespace.neuron.di.java.CachingStrategy.THREAD_SAFE;

/**
 * Adapts an underlying {@link Log} to the {@link Logger} interface.
 */
@Neuron(cachingStrategy = THREAD_SAFE)
abstract class MojoLogger implements Logger {

    /**
     * Returns the Maven logger which can be wired using {@link org.apache.maven.plugin.AbstractMojo}.
     */
    abstract Log getLog();

    @Override
    public void debug(Throwable error) {
        getLog().debug(error);
    }

    @Override
    @Caching
    public boolean isDebugEnabled() {
        return getLog().isDebugEnabled();
    }

    @Override
    public void debug(CharSequence message) {
        getLog().debug(message);
    }

    @Override
    public void debug(CharSequence message, Throwable error) {
        getLog().debug(message, error);
    }

    @Override
    @Caching
    public boolean isInfoEnabled() {
        return getLog().isInfoEnabled();
    }

    @Override
    public void info(Throwable error) {
        getLog().info(error);
    }

    @Override
    public void info(CharSequence message) {
        getLog().info(message);
    }

    @Override
    public void info(CharSequence message, Throwable throwable) {
        getLog().info(message, throwable);
    }

    @Override
    @Caching
    public boolean isWarnEnabled() {
        return getLog().isWarnEnabled();
    }

    @Override
    public void warn(Throwable error) {
        getLog().warn(error);
    }

    @Override
    public void warn(CharSequence message) {
        getLog().warn(message);
    }

    @Override
    public void warn(CharSequence message, Throwable error) {
        getLog().warn(message, error);
    }

    @Override
    @Caching
    public boolean isErrorEnabled() {
        return getLog().isErrorEnabled();
    }

    @Override
    public void error(Throwable error) {
        getLog().error(error);
    }

    @Override
    public void error(CharSequence message) {
        getLog().error(message);
    }

    @Override
    public void error(CharSequence message, Throwable error) {
        getLog().error(message, error);
    }
}
