/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.maven.plugin.commons;

import global.namespace.truelicense.build.tasks.commons.AbstractTask;
import global.namespace.truelicense.build.tasks.commons.Logger;
import global.namespace.truelicense.build.tasks.commons.Task;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

import static global.namespace.neuron.di.java.Incubator.wire;

/**
 * An abstract base class for implementing a Maven plugin which checks exceptions and error log entries.
 * This class is not thread-safe.
 */
public abstract class BasicMojo extends AbstractMojo {

    private Logger logger;

    @Override
    public void setLog(final Log log) {
        logger = null;
        super.setLog(log);
    }

    /**
     * Returns the adapted logger which can be used to wire {@link AbstractTask}.
     */
    // This class is instantiated by Maven, so it cannot be a @Neuron and hence I cannot use @Caching here.
    // Also, tasks and mojos are generally not thread-safe, so this method doesn't need to, too.
    Logger logger() {
        final Logger logger = this.logger;
        return null != logger ? logger : (this.logger = wire(MojoLogger.class).using(this));
    }

    @Override
    public final void execute() throws MojoExecutionException, MojoFailureException {
        try {
            preExecute();
            task().execute();
            postExecute();
        } catch (RuntimeException e) {
            throw new MojoExecutionException(e.toString(), e);
        } catch (Exception e) {
            throw new MojoFailureException(e.toString(), e);
        }
    }

    @SuppressWarnings({"RedundantThrows", "WeakerAccess"})
    protected void preExecute() throws Exception {
    }

    protected abstract Task task();

    protected void postExecute() throws Exception {
    }
}
