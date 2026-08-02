/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.build.tasks.commons;

/**
 * A common base class for build tasks.
 */
public abstract class AbstractTask implements Task {

    /**
     * Returns the logger used by this build task.
     */
    public abstract Logger logger();
}
