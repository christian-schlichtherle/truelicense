/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.api;

/**
 * A factory for licenses.
 */
public interface LicenseFactory {

    /**
     * Returns a new license.
     */
    License license();

    /**
     * Returns the base class of all licenses created by this factory.
     */
    Class<? extends License> licenseClass();
}
