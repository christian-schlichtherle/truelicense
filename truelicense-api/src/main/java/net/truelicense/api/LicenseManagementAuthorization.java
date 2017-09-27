/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api;

import net.truelicense.api.io.Sink;
import net.truelicense.api.io.Source;

/**
 * Defines an authorization for the license key life cycle management operations.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseManagementAuthorization {

    /**
     * Returns if and only if saving a license key is authorized.
     * This method is normally only called in a license vendor application.
     * However, in a license consumer application, this method gets called if a non-zero FTP is configured and no FTP
     * license key has been generated yet.
     * In this case, the implementation may perform a test if the consumer is eligible to generate a new FTP license key
     * in order to protect against a malicious removal of the auto-generated FTP license key.
     *
     * @param schema the licensing schema.
     * @see LicenseKeyGenerator#saveTo(Sink)
     */
    void clearGenerate(LicenseManagementSchema schema) throws Exception;

    /**
     * Returns if and only if installing a license key is authorized.
     *
     * @param schema the licensing schema.
     * @see ConsumerLicenseManager#install(Source)
     */
    void clearInstall(LicenseManagementSchema schema) throws Exception;

    /**
     * Returns if and only if loading a license key is authorized.
     *
     * @param schema the licensing schema.
     * @see ConsumerLicenseManager#load()
     */
    void clearLoad(LicenseManagementSchema schema) throws Exception;

    /**
     * Returns if and only if verifying a license key is authorized.
     *
     * @param schema the licensing schema.
     * @see ConsumerLicenseManager#verify()
     */
    void clearVerify(LicenseManagementSchema schema) throws Exception;

    /**
     * Returns if and only if uninstalling a license key is authorized.
     *
     * @param schema the licensing schema.
     * @see ConsumerLicenseManager#uninstall()
     */
    void clearUninstall(LicenseManagementSchema schema) throws Exception;
}
