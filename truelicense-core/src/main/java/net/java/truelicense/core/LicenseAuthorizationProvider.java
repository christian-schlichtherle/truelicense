/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.core;

/**
 * Provides a license authorization.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseAuthorizationProvider {

    /** Returns the license authorization. */
    LicenseAuthorization authorization();
}
