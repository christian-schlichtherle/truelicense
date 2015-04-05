/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

/**
 * Defines an authorization for the license key life cycle management
 * operations.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseAuthorization {

    /**
     * Returns if and only if
     * {@linkplain LicenseVendorManager#create creating}
     * a license key is authorized.
     * In a license consumer application, this method gets called if a non-zero
     * FTP is configured and no FTP license key has been generated yet.
     * In this case, the implementation may perform a test if the consumer is
     * eligible to create a new FTP license key in order to protect against a
     * malicious removal of the auto-generated FTP license key.
     *
     * @param lp the license parameters.
     */
    void clearCreate(LicenseParameters lp) throws Exception;

    /**
     * Returns if and only if
     * {@linkplain LicenseConsumerManager#install installing}
     * a license key is authorized.
     *
     * @param lp the license parameters.
     */
    void clearInstall(LicenseParameters lp) throws Exception;

    /**
     * Returns if and only if
     * {@linkplain LicenseConsumerManager#verify verifying}
     * a license key is authorized.
     *
     * @param lp the license parameters.
     */
    void clearVerify(LicenseParameters lp) throws Exception;

    /**
     * Returns if and only if
     * {@linkplain LicenseConsumerManager#view viewing}
     * a license key is authorized.
     *
     * @param lp the license parameters.
     */
    void clearView(LicenseParameters lp) throws Exception;

    /**
     * Returns if and only if
     * {@linkplain LicenseConsumerManager#uninstall uninstalling}
     * a license key is authorized.
     *
     * @param lp the license parameters.
     */
    void clearUninstall(LicenseParameters lp) throws Exception;
}
