/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package global.namespace.truelicense.api;

/**
 * Defines a license initialization.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseInitialization {

    /** Initializes the properties of the given license bean. */
    void initialize(License bean);
}
