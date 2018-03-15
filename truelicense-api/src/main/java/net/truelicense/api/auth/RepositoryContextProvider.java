/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api.auth;

/**
 * Provides a repository context.
 *
 * @author Christian Schlichtherle
 */
public interface RepositoryContextProvider {

    /** Returns a repository context. */
    <Model> RepositoryContext<Model> repositoryContext();
}
