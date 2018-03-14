/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api;

/**
 * Defines the life cycle management operations for license keys in vendor applications alias key generators.
 * <p>
 * An unchecked vendor license manager generally throws an {@link UncheckedLicenseManagementException} with a
 * (checked) {@link LicenseManagementException} as its cause if an operation fails for some reason.
 *
 * @see VendorLicenseManager#unchecked()
 * @author Christian Schlichtherle
 */
public interface UncheckedVendorLicenseManager extends LicenseManagementSchema {

    /**
     * Returns a license key generator for the given license bean.
     * <p>
     * Calling this operation performs an initial
     * {@linkplain LicenseManagementAuthorization#clearGenerate authorization check}.
     *
     * @param bean the license bean to process.
     *             This bean is not modified by the returned license key generator.
     * @return A license key generator for the given license bean.
     */
    UncheckedLicenseKeyGenerator generateKeyFrom(License bean) throws UncheckedLicenseManagementException;

    /**
     * Adapts this vendor license manager so that it generally throws a (checked) {@link LicenseManagementException}
     * instead of an {@link UncheckedLicenseManagementException} if an operation fails.
     *
     * @return the adapted (checked) vendor license manager.
     * @since TrueLicense 3.1.0
     */
    VendorLicenseManager checked();
}
