/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api;

/**
 * Defines an authorization for license key life cycle management operations.
 * The implementation in this class authorizes all operations.
 *
 * @author Christian Schlichtherle
 */
public class LicenseManagementAuthorization {

    /**
     * Returns if and only if saving a license key is authorized.
     * This method is normally only called in a license vendor application.
     * However, in a license consumer application, this method gets called if a non-zero FTP is configured and no FTP
     * license key has been generated yet.
     * In this case, the implementation may perform a test if the consumer is eligible to generate a new FTP license key
     * in order to protect against a malicious removal of the auto-generated FTP license key.
     *
     * @param schema the licensing schema.
     * @see VendorLicenseManager#generateKeyFrom(License)
     */
    public void clearGenerate(LicenseManagementSchema schema) throws Exception { }

    /**
     * Returns if and only if installing a license key is authorized.
     *
     * @param schema the licensing schema.
     * @see ConsumerLicenseManager#install(global.namespace.fun.io.api.Source)
     */
    public void clearInstall(LicenseManagementSchema schema) throws Exception { }

    /**
     * Returns if and only if loading a license key is authorized.
     *
     * @param schema the licensing schema.
     * @see ConsumerLicenseManager#load()
     */
    public void clearLoad(LicenseManagementSchema schema) throws Exception { }

    /**
     * Returns if and only if verifying a license key is authorized.
     *
     * @param schema the licensing schema.
     * @see ConsumerLicenseManager#verify()
     */
    public void clearVerify(LicenseManagementSchema schema) throws Exception { }

    /**
     * Returns if and only if uninstalling a license key is authorized.
     *
     * @param schema the licensing schema.
     * @see ConsumerLicenseManager#uninstall()
     */
    public void clearUninstall(LicenseManagementSchema schema) throws Exception { }
}
