/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.auth;

/**
 * Provides an authentication.
 *
 * @author Christian Schlichtherle
 */
public interface AuthenticationProvider {

    /** Returns the authentication. */
    Authentication authentication();
}
