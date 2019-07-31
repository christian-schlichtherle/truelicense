/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.build.tasks.commons;

import global.namespace.neuron.di.java.Neuron;

import static global.namespace.neuron.di.java.CachingStrategy.NOT_THREAD_SAFE;

/**
 * A common base class for build tasks.
 */
@Neuron(cachingStrategy = NOT_THREAD_SAFE)
public abstract class AbstractTask implements Task {

    /**
     * Returns the logger used by this build task.
     */
    public abstract Logger logger();
}
