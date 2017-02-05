/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

/**
 * Provides a license validation.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseValidationProvider {

    /** Returns the license validation. */
    LicenseValidation validation();
}
