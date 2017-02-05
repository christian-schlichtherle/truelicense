/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.auth;

import org.truelicense.api.codec.Codec;

/**
 * A factory for repository models and their associated
 * {@linkplain RepositoryController repository controllers}.
 * A repository context is created by a
 * {@linkplain RepositoryContextProvider repository context provider}.
 *
 * @param <Model> the generic repository model.
 */
public interface RepositoryContext<Model> {

    /** Returns a new repository model. */
    Model model();

    /**
     * Returns a repository controller for the given repository model using the
     * given codec.
     */
    RepositoryController controller(Model model, Codec codec);
}
