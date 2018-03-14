/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api.auth;

/**
 * Provides an authentication function.
 *
 * @author Christian Schlichtherle
 */
public interface AuthenticationFunctionProvider {

    /** Returns the authentication function. */
    AuthenticationFunction authenticationFunction();
}
