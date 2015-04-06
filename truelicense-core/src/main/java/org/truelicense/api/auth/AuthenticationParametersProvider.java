/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.auth;

/**
 * Provides authentication parameters.
 *
 * @author Christian Schlichtherle
 */
public interface AuthenticationParametersProvider {
    /** Returns the authentication parameters. */
    AuthenticationParameters parameters();
}
