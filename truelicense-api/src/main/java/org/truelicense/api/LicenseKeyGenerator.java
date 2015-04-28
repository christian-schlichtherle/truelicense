/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

import org.truelicense.api.io.Sink;

import java.io.IOException;

/**
 * A service to write a generated license key to a given sink and return a
 * duplicate of its encoded license bean.
 * License key generators are the product of a call to
 * {@link LicenseVendorManager#generator(License)}.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseKeyGenerator {

    /**
     * Returns a duplicate of the
     * {@linkplain LicenseInitialization#initialize initialized}
     * and
     * {@linkplain LicenseValidation#validate validated}
     * license bean which gets encoded in the generated license key.
     *
     * @throws LicenseManagementException if duplicating the license bean fails
     *                                    for some reason, e.g. if the codec
     *                                    does not supported a custom property
     *                                    type.
     */
    License license() throws LicenseManagementException;

    /**
     * Writes the generated license key to the given sink.
     *
     * @param sink the sink for writing the generated license key to.
     * @return {@code this}
     * @throws LicenseManagementException if writing the license key fails for
     *                                    some reason, e.g. if there is an
     *                                    {@link IOException}.
     */
    LicenseKeyGenerator writeTo(Sink sink) throws LicenseManagementException;
}
