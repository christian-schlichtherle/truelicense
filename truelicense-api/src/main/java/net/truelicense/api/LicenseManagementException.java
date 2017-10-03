/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api;

import java.security.GeneralSecurityException;

/**
 * Indicates an issue when
 * {@linkplain VendorLicenseManager#generateKeyFrom generating},
 * {@linkplain ConsumerLicenseManager#install installing},
 * {@linkplain ConsumerLicenseManager#load loading},
 * {@linkplain ConsumerLicenseManager#verify verifying} or
 * {@linkplain ConsumerLicenseManager#uninstall uninstalling} a license key.
 *
 * @author Christian Schlichtherle
 */
public class LicenseManagementException extends GeneralSecurityException {

    private static final long serialVersionUID = 0L;

    public LicenseManagementException() { }

    public LicenseManagementException(Throwable cause) { super(cause); }

    /**
     * Returns {@code true} if this exception is considered confidential and
     * should not be shared with users.
     * <p>
     * The implementation in the class {@code LicenseManagementException}
     * returns {@code true}.
     *
     * @since TrueLicense 2.3 (renamed from isConsideredConfidential() in TrueLicense 3.0)
     */
    public boolean isConfidential() { return true; }
}
