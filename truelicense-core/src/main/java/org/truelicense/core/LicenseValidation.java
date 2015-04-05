/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

/**
 * Defines a license validation.
 *
 * @see    LicenseValidationProvider
 * @author Christian Schlichtherle
 */
public interface LicenseValidation {

    /** Validates the properties of the given license bean. */
    void validate(License bean) throws LicenseValidationException;
}
