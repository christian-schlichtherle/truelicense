/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

import org.truelicense.api.io.Sink;

/**
 * @author Christian Schlichtherle
 */
public interface UncheckedLicenseKeyGenerator extends LicenseKeyGenerator {

    @Override
    License license() throws UncheckedLicenseManagementException;

    @Override
    LicenseKeyGenerator writeTo(Sink sink) throws UncheckedLicenseManagementException;
}
