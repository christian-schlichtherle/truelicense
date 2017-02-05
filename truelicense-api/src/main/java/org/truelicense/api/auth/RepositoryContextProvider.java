/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.auth;

/**
 * Provides a repository context.
 *
 * @param <Model> the generic repository model.
 * @author Christian Schlichtherle
 */
public interface RepositoryContextProvider<Model> {

    /** Returns a repository context. */
    RepositoryContext<Model> repositoryContext();
}
