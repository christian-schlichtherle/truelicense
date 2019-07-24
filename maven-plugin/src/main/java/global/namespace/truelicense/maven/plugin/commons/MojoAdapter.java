/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.maven.plugin.commons;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

/**
 * An abstract base class for implementing a Maven plugin which checks
 * exceptions and error log entries.
 * This class is thread-safe.
 *
 * @see #execute()
 * @author Christian Schlichtherle
 */
public abstract class MojoAdapter extends AbstractMojo {

    private volatile CheckedLog checkedLog;

    /**
     * Calls {@link #doExecute()}, checking exceptions and error log entries.
     * If {@code doExecute()} throws a {@link MojoExecutionException} or a
     * {@link MojoFailureException}, then a new error log entry gets generated
     * and the exception gets rethrown.
     * Otherwise, if any error log entries have been previously generated,
     * then a new {@code MojoFailureException} gets thrown.
     *
     * @throws MojoExecutionException If {@link #doExecute()} throws an
     *         exception of this type.
     * @throws MojoFailureException If {@link #doExecute()} throws an
     *         exception of this type or if any error log entries have been
     *         generated using the logger returned by {@link #getLog()}.
     */
    @Override
    public final void execute()
    throws MojoExecutionException, MojoFailureException {
        try {
            doExecute();
        } catch (final Throwable e) {
            getLog().error(e.toString(), e);
            throw e;
        }
        if (null != checkedLog) {
            checkedLog.check();
        }
    }

    /**
     * Runs this MOJO.
     *
     * @throws MojoExecutionException If you want to stop the build as soon as possible.
     * @throws MojoFailureException If you want to stop the build as late as possible.
     */
    protected abstract void doExecute()
    throws MojoExecutionException, MojoFailureException;

    @Override
    public void setLog(final Log log) {
        super.setLog(log);
        checkedLog = null;
    }

    @Override
    public Log getLog() {
        CheckedLog cl = checkedLog;
        if (null != cl) {
            return cl;
        }
        synchronized (this) {
            cl = checkedLog;
            return null != cl ? cl : (checkedLog = new CheckedLog(super.getLog()));
        }
    }
}
