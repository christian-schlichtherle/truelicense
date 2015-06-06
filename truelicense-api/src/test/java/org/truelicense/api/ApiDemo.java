/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

import org.truelicense.api.io.Source;
import org.truelicense.api.io.Store;
import org.truelicense.api.misc.ClassLoaderProvider;
import org.truelicense.api.misc.Clock;
import org.truelicense.api.passwd.PasswordProtection;

import java.nio.file.Path;

import static org.mockito.Mockito.mock;

/**
 * A demo which uses as many TrueLicense API calls as possible.
 * This is not a working demo - do not use this code in production!
 *
 * @author Christian Schlichtherle
 */
public abstract class ApiDemo {

    public abstract LicenseApplicationContext licenseApplicationContext();

    /**
     * Returns a license management context.
     * The returned license management context has been configured using the
     * given license application context and can be used to configure vendor
     * or consumer license managers or create their dependencies, e.g. a store
     * for the license key to generate or install.
     */
    public LicenseManagementContext licenseManagementContext() {
        return licenseApplicationContext()
                .context() // returns a LicenseManagementContextBuilder
                .authorization(mock(LicenseManagementAuthorization.class))
                .classLoaderProvider(mock(ClassLoaderProvider.class))
                .clock(mock(Clock.class))
                .initialization(mock(LicenseInitialization.class))
                .initializationComposition(LicenseFunctionComposition.decorate)
                .subject("MyProduct 1") // required call
                .validation(mock(LicenseValidation.class))
                .validationComposition(LicenseFunctionComposition.decorate)
                .build();
    }

    /**
     * Returns a license manager for a vendor application, i.e. your license key
     * generator.
     * The returned vendor license manager has been configured using the given
     * license management context and can be used to generate license keys.
     */
    public VendorLicenseManager vendorLicenseManager() {
        return licenseManagementContext()
                .vendor() // returns a VendorLicenseManagerBuilder
                .encryption()
                    .algorithm("PBEWithSHA1AndDESede")
                    .protection(mock(PasswordProtection.class))
                    .inject()
                .authentication()
                    .algorithm("RSA")
                    .alias("mykey")
                    .keyProtection(mock(PasswordProtection.class))
                    .loadFrom(mock(Source.class))
                    .loadFromResource("private.ks")
                    .storeProtection(mock(PasswordProtection.class))
                    .storeType("JCEKS")
                    .inject()
                .build();
    }

    /**
     * Returns a license manager for a consumer application, i.e. your software
     * product.
     * The returned consumer license manager has been configured using the given
     * license management context and can be used to install, view, verify and
     * uninstall license keys.
     */
    public ConsumerLicenseManager consumerLicenseManager() {
        return licenseManagementContext()
                .consumer() // returns a ConsumerLicenseManagerBuilder
                .ftpDays(30)
                .encryption()
                    .algorithm("PBEWithSHA1AndDESede")
                    .protection(mock(PasswordProtection.class))
                    .inject()
                .authentication()
                    .algorithm("RSA")
                    .alias("mykey")
                    .keyProtection(mock(PasswordProtection.class))
                    .loadFrom(mock(Source.class))
                    .loadFromResource("private.ks")
                    .storeProtection(mock(PasswordProtection.class))
                    .storeType("JCEKS")
                    .inject()
                .parent(mock(ConsumerLicenseManager.class))
                .storeIn(mock(Store.class))
                .storeInPath(mock(Path.class))
                .storeInSystemPreferences(Class.class)
                .storeInUserPreferences(Class.class)
                .build();
    }
}
