/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core;

/**
 * Provides a license initialization.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseInitializationProvider {

    /** Returns the license initialization. */
    LicenseInitialization initialization();
}
