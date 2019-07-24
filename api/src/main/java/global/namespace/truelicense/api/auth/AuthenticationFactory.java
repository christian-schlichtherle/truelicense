/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.api.auth;

/** Creates an authentication from some given parameters. */
public interface AuthenticationFactory {

    /** Returns an authentication from the given parameters. */
    Authentication authentication(AuthenticationParameters authenticationParameters);
}
