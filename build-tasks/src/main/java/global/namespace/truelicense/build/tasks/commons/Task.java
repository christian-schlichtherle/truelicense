/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.build.tasks.commons;

/**
 * A build task is typically used by a build plugin.
 * A task is typically <em>not</em> thread-safe and its side-effects are typically <em>not</em> idempotent.
 */
public interface Task {

    /**
     * Executes this build task.
     * A task is typically <em>not</em> thread-safe and its side-effects are typically <em>not</em> idempotent.
     */
    void execute() throws Exception;
}
