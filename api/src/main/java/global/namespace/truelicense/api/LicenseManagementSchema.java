/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.api;

import global.namespace.fun.io.api.Filter;
import global.namespace.truelicense.api.auth.Authentication;

/** A schema for license management. */
public interface LicenseManagementSchema {

    /** Returns the authentication. */
    Authentication authentication();

    /** Returns the license management context from which this license management schema originated. */
    LicenseManagementContext context();

    /** Returns the password based encryption transformation. */
    Filter encryption();
}
