/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api.auth;

/**
 * Creates an authentication from some given parameters.
 *
 * @author Christian Schlichtherle
 */
public interface AuthenticationFactory {

    /** Returns an authentication from the given parameters. */
    Authentication authentication(AuthenticationParameters authenticationParameters);
}
