/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

/**
 * Provides the license management parameters.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseManagementParametersProvider {

    /** Returns the license management parameters. */
    LicenseManagementParameters parameters();
}
