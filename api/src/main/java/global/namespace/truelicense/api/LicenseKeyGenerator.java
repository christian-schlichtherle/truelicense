/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.api;

import global.namespace.fun.io.api.Sink;

/**
 * Generates a license key and writes it to a given sink or returns a duplicate of its encoded license bean.
 * License key generators are the product of a call to {@link VendorLicenseManager#generateKeyFrom(License)}.
 * License key generators are stateful and so they are generally <em>not</em> thread-safe.
 * <p>
 * When the license key gets generated, a protective copy of the configured license bean is made which is subsequently
 * {@linkplain LicenseInitialization#initialize initialized} and {@linkplain LicenseValidation#validate validated}.
 */
public interface LicenseKeyGenerator {

    /**
     * Returns a duplicate of the license bean which is encoded in the generated license key.
     *
     * @throws LicenseManagementException if generating the license key fails, e.g. if initializing or validating the
     *         license bean fails.
     */
    License license() throws LicenseManagementException;

    /**
     * Saves the generated license key to the given sink.
     *
     * @param sink the sink to write the generated license key to.
     * @return {@code this}
     * @throws LicenseManagementException if generating the license key fails or it cannot be written to the sink.
     */
    LicenseKeyGenerator saveTo(Sink sink) throws LicenseManagementException;
}
