/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.truelicense.api;

import global.namespace.fun.io.api.Sink;

import static net.truelicense.api.UncheckedLicenseManager.callUnchecked;

/**
 * Defines the life cycle management operations for license keys in vendor applications alias key generators.
 * <p>
 * An unchecked vendor license manager generally throws an {@link UncheckedLicenseManagementException} with a
 * (checked) {@link LicenseManagementException} as its cause if an operation fails for some reason.
 *
 * @author Christian Schlichtherle
 * @see VendorLicenseManager#unchecked()
 */
public interface UncheckedVendorLicenseManager extends LicenseManagerFragment {

    /**
     * Adapts this vendor license manager so that it generally throws a (checked) {@link LicenseManagementException}
     * instead of an {@link UncheckedLicenseManagementException} if an operation fails.
     *
     * @return the adapted (checked) vendor license manager.
     * @since TrueLicense 3.1.0
     */
    VendorLicenseManager checked();

    @Override
    default LicenseManagementSchema schema() {
        return checked().schema();
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
    default UncheckedLicenseKeyGenerator generateKeyFrom(License bean) throws UncheckedLicenseManagementException {
        return callUnchecked(() -> new UncheckedLicenseKeyGenerator() {
            final LicenseKeyGenerator generator = checked().generateKeyFrom(bean);

            @Override
            public License license() throws UncheckedLicenseManagementException {
                return callUnchecked(generator::license);
            }

            @Override
            public UncheckedLicenseKeyGenerator saveTo(Sink sink) throws UncheckedLicenseManagementException {
                callUnchecked(() -> generator.saveTo(sink));
                return this;
            }
        });
    }
}
