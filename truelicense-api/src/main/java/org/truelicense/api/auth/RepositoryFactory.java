/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.auth;

/**
 * Creates a new repository.
 *
 * @author Christian Schlichtherle
 */
public interface RepositoryFactory {

    /** Returns a new repository. */
    Repository repository();
}
