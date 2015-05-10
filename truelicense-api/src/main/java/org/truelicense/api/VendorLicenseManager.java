/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

/**
 * Defines the life cycle management operations for license keys in vendor
 * applications alias key generators.
 *
 * @author Christian Schlichtherle
 */
public interface VendorLicenseManager extends LicenseManagementSchema {

    /**
     * Returns a license key generator for the given license bean.
     * <p/>
     * Calling this operation performs an initial
     * {@linkplain LicenseManagementAuthorization#clearGenerator authorization check}.
     *
     * @param bean the license bean to process.
     *             This bean is not modified by the returned license key
     *             generator.
     *             Instead, a protective copy is made which is subsequently
     *             {@linkplain LicenseInitialization#initialize initialized}
     *             and
     *             {@linkplain LicenseValidation#validate validated}.
     * @return A license key generator for the given license bean.
     */
    LicenseKeyGenerator generator(License bean) throws LicenseManagementException;
}
