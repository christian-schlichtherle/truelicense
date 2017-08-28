/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api;

import net.truelicense.api.io.Sink;

/**
 * A license key generator which generally throws an
 * {@link UncheckedLicenseManagementException} rather than a (checked)
 * {@link LicenseManagementException}.
 *
 * @see UncheckedVendorLicenseManager#generator(License)
 * @author Christian Schlichtherle
 */
public interface UncheckedLicenseKeyGenerator extends LicenseKeyGenerator {

    @Override
    LicenseKeyGenerator save(Sink sink) throws UncheckedLicenseManagementException;

    @Override
    License license() throws UncheckedLicenseManagementException;
}
