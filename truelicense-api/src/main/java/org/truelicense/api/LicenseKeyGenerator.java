/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

import org.truelicense.api.io.Sink;

/**
 * Generates a license key and writes it to a given sink or returns a duplicate
 * of its encoded license bean.
 * License key generators are the product of a call to
 * {@link LicenseVendorManager#generator(License)}.
 * License key generators are stateful and so they are generally not
 * thread-safe.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseKeyGenerator {

    /**
     * Returns a duplicate of the license bean which is encoded in the
     * generated license key.
     */
    License license() throws LicenseManagementException;

    /**
     * Writes the generated license key to the given sink.
     *
     * @param sink the sink for writing the generated license key to.
     * @return {@code this}
     */
    LicenseKeyGenerator writeTo(Sink sink) throws LicenseManagementException;
}
