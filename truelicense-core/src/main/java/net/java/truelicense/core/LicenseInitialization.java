/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core;

/**
 * Defines a license initialization.
 *
 * @see    LicenseInitializationProvider
 * @author Christian Schlichtherle
 */
public interface LicenseInitialization {

    /** Initializes the properties of the given license bean. */
    void initialize(License bean);
}
