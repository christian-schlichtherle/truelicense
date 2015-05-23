/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

import org.mockito.Mockito;
import org.truelicense.api.passwd.PasswordProtection;

/**
 * @author Christian Schlichtherle
 */
public class ApiDemo {

    /**
     * Returns a license management context.
     * The returned license management context has been configured using the
     * given license application context and can be used to configure vendor
     * or consumer license managers or create their dependencies, e.g. a store
     * for the license key to generate or install.
     */
    static LicenseManagementContext licenseManagementContext(LicenseApplicationContext applicationContext) {
        return applicationContext
                .context() // returns a LicenseManagementContextBuilder
                //.authorization(new CustomLicenseAuthorization())
                //.clock(new CustomClock())
                //.initialization(new CustomLicenseInitialization())
                //.initializationComposition(LicenseFunctionComposition.decorate)
                .subject("MyProduct 1") // required call
                //.validation(new CustomLicenseValidation())
                //.validationComposition(LicenseFunctionComposition.decorate)
                .build();
    }

    /**
     * Returns a license manager for a vendor application, i.e. your license key
     * generator.
     * The returned vendor license manager has been configured using the given
     * license management context and can be used to generate license keys.
     */
    static VendorLicenseManager vendorLicenseManager(LicenseManagementContext managementContext) {
        return managementContext
                .vendor() // returns a VendorLicenseManagerBuilder
                .encryption()
                    //.algorithm("PBEWithSHA1AndDESede")
                    .protection(somePasswordProtection())
                    .inject()
                .authentication()
                    //.algorithm("RSA")
                    .alias("mykey")
                    //.keyProtection(somePasswordProtection())
                    .loadFromResource("private.ks")
                    .storeProtection(somePasswordProtection())
                    //.storeType("JCEKS")
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
    static ConsumerLicenseManager consumerLicenseManager(LicenseManagementContext managementContext) {
        return managementContext
                .consumer() // returns a ConsumerLicenseManagerBuilder
                //.ftpDays(30)
                .encryption()
                    //.algorithm("PBEWithSHA1AndDESede")
                    .protection(somePasswordProtection())
                    .inject()
                .authentication()
                    //.algorithm("RSA")
                    .alias("mykey")
                    //.keyProtection(somePasswordProtection())
                    .loadFromResource("private.ks")
                    .storeProtection(somePasswordProtection())
                    //.storeType("JCEKS")
                    .inject()
                //.parent(parent())
                //.storeIn(someStore())
                //.storeInPath(somePath())
                //.storeInSystemPreferences(someClazzInPackage())
                .storeInUserPreferences(someClazzInPackage())
                .build();
    }

    static PasswordProtection somePasswordProtection() {
        return Mockito.mock(PasswordProtection.class);
    }

    static Class<?> someClazzInPackage() { return ApiDemo.class; }
}
