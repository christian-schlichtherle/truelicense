/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api;

/**
 * Provides a license authorization.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseManagementAuthorizationProvider {

    /** Returns the license authorization. */
    LicenseManagementAuthorization authorization();
}
