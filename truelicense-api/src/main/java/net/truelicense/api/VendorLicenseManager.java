/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.truelicense.api;

/**
 * Defines the life cycle management operations for license keys in vendor applications alias key generators.
 * <p>
 * A (checked) vendor license manager generally throws a {@link LicenseManagementException} if an operation fails.
 *
 * @author Christian Schlichtherle
 * @see UncheckedVendorLicenseManager#checked()
 */
public interface VendorLicenseManager extends LicenseManagerMixin {

    /**
     * Adapts this vendor license manager so that it generally throws an {@link UncheckedLicenseManagementException}
     * instead of a (checked) {@link LicenseManagementException} if an operation fails.
     *
     * @return the adapted unchecked vendor license manager.
     * @since TrueLicense 3.1.0
     */
    default UncheckedVendorLicenseManager unchecked() {
        return () -> this;
    }

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
    LicenseKeyGenerator generateKeyFrom(License bean) throws LicenseManagementException;
}
