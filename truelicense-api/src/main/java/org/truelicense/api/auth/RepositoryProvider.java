/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.auth;

/**
 * Provides a repository.
 *
 * @author Christian Schlichtherle
 */
public interface RepositoryProvider {

    /** Returns the repository. */
    Repository repository();
}
