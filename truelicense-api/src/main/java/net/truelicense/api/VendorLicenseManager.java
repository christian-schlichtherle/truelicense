/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api;

/**
 * Defines the life cycle management operations for license keys in vendor applications alias key generators.
 *
 * @author Christian Schlichtherle
 */
public interface VendorLicenseManager extends LicenseManagementSchema {

    /**
     * Returns a license key generator for the given license bean.
     *
     * @param bean the license bean to process.
     *             This bean is not modified by the returned license key generator.
     * @return A license key generator for the given license bean.
     */
    LicenseKeyGenerator generateKeyFrom(License bean);
}
