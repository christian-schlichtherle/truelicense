/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api;

/**
 * Defines an authorization for the license key life cycle management
 * operations.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseManagementAuthorization {

    /**
     * Returns if and only if
     * {@linkplain LicenseKeyGenerator#save saving}
     * a license key is authorized.
     * This method is normally only called in a license vendor application.
     * However, in a license consumer application, this method gets called if
     * a non-zero FTP is configured and no FTP license key has been generated
     * yet.
     * In this case, the implementation may perform a test if the consumer is
     * eligible to generate a new FTP license key in order to protect against a
     * malicious removal of the auto-generated FTP license key.
     *
     * @param schema the licensing schema.
     */
    void clearSave(LicenseManagementSchema schema) throws Exception;

    /**
     * Returns if and only if
     * {@linkplain ConsumerLicenseManager#install installing}
     * a license key is authorized.
     *
     * @param schema the licensing schema.
     */
    void clearInstall(LicenseManagementSchema schema) throws Exception;

    /**
     * Returns if and only if
     * {@linkplain ConsumerLicenseManager#load loading}
     * a license key is authorized.
     *
     * @param schema the licensing schema.
     */
    void clearLoad(LicenseManagementSchema schema) throws Exception;

    /**
     * Returns if and only if
     * {@linkplain ConsumerLicenseManager#verify verifying}
     * a license key is authorized.
     *
     * @param schema the licensing schema.
     */
    void clearVerify(LicenseManagementSchema schema) throws Exception;

    /**
     * Returns if and only if
     * {@linkplain ConsumerLicenseManager#uninstall uninstalling}
     * a license key is authorized.
     *
     * @param schema the licensing schema.
     */
    void clearUninstall(LicenseManagementSchema schema) throws Exception;
}
