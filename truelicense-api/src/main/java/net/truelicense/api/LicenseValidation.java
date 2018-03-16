/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api;

/**
 * Defines a license validation.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseValidation {

    /** Validates the properties of the given license bean. */
    void validate(License bean) throws LicenseValidationException;
}
