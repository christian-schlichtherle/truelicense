/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.api;

/** Defines an authorization for license key life cycle management operations. */
public interface LicenseManagementAuthorization {

    /**
     * This instances authorizes all operations.
     */
    LicenseManagementAuthorization ALL = new LicenseManagementAuthorization() {
    };

    /**
     * Returns if and only if saving a license key is authorized.
     * This method is normally only called in a license vendor application.
     * However, in a license consumer application, this method gets called if a non-zero FTP is configured and no FTP
     * license key has been generated yet.
     * In this case, the implementation may perform a test if the consumer is eligible to generate a new FTP license key
     * in order to protect against a malicious removal of the auto-generated FTP license key.
     *
     * @param manager the vendor license manager.
     * @see VendorLicenseManager#generateKeyFrom(License)
     */
    default void clearGenerate(VendorLicenseManager manager) throws Exception {
    }

    /**
     * Returns if and only if installing a license key is authorized.
     *
     * @param manager the consumer license manager.
     * @see ConsumerLicenseManager#install(global.namespace.fun.io.api.Source)
     */
    default void clearInstall(ConsumerLicenseManager manager) throws Exception {
    }

    /**
     * Returns if and only if loading a license key is authorized.
     *
     * @param manager the consumer license manager.
     * @see ConsumerLicenseManager#load()
     */
    default void clearLoad(ConsumerLicenseManager manager) throws Exception {
    }

    /**
     * Returns if and only if verifying a license key is authorized.
     *
     * @param manager the consumer license manager.
     * @see ConsumerLicenseManager#verify()
     */
    default void clearVerify(ConsumerLicenseManager manager) throws Exception {
    }

    /**
     * Returns if and only if uninstalling a license key is authorized.
     *
     * @param manager the consumer license manager.
     * @see ConsumerLicenseManager#uninstall()
     */
    default void clearUninstall(ConsumerLicenseManager manager) throws Exception {
    }
}
