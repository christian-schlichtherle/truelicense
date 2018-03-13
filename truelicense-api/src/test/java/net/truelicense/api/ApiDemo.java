/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api;

import global.namespace.fun.io.api.Socket;
import global.namespace.fun.io.api.Store;
import global.namespace.fun.io.api.Transformation;
import net.truelicense.api.auth.Authentication;
import net.truelicense.api.misc.Clock;
import net.truelicense.api.passwd.PasswordPolicy;
import net.truelicense.api.passwd.PasswordProtection;

import java.io.InputStream;
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
                .cachePeriodMillis(1000L)
                .clock(mock(Clock.class))
                .initialization(mock(LicenseInitialization.class))
                .initializationComposition(LicenseFunctionComposition.decorate)
                .passwordPolicy(mock(PasswordPolicy.class))
                .subject("MyProduct 1")
                .validation(mock(LicenseValidation.class))
                .validationComposition(LicenseFunctionComposition.decorate)
                .build();
    }

    /**
     * Returns a license manager for a license vendor application, i.e. your
     * license key generator.
     * The returned vendor license manager has been configured using the given
     * license management context and can be used to generate and save license
     * keys.
     */
    public VendorLicenseManager vendorLicenseManager() {
        return licenseManagementContext()
                .vendor() // returns a VendorLicenseManagerBuilder
                .encryption() // returns an EncryptionBuilder
                    .algorithm("PBEWithSHA1AndDESede")
                    .protection(mock(PasswordProtection.class))
                    .up() // builds the encryption, injects it into the VendorLicenseManagerBuilder and returns the latter
                .encryption(mock(Transformation.class))
                .authentication() // returns an AuthenticationBuilder
                    .algorithm("RSA")
                    .alias("mykey")
                    .keyProtection(mock(PasswordProtection.class))
                    .loadFrom((Socket<InputStream>) mock(Socket.class))
                    .loadFromResource("private.ks")
                    .storeProtection(mock(PasswordProtection.class))
                    .storeType("JCEKS")
                    .up() // builds the authentication, injects it into the VendorLicenseManagerBuilder and returns the latter
                .authentication(mock(Authentication.class))
                .build();
    }

    /**
     * Returns a license manager for a license consumer application, i.e. your
     * software product.
     * The returned consumer license manager has been configured using the given
     * license management context and can be used to install, load, verify and
     * uninstall license keys.
     */
    public ConsumerLicenseManager consumerLicenseManager() {
        return licenseManagementContext()
                .consumer() // returns a ConsumerLicenseManagerBuilder
                .ftpDays(30)
                .encryption() // returns an EncryptionBuilder
                    .algorithm("PBEWithSHA1AndDESede")
                    .protection(mock(PasswordProtection.class))
                    .up() // builds the encryption, injects it into the ConsumerLicenseManagerBuilder and returns the latter
                .encryption(mock(Transformation.class))
                .authentication() // returns an AuthenticationBuilder
                    .algorithm("RSA")
                    .alias("mykey")
                    .keyProtection(mock(PasswordProtection.class))
                    .loadFrom((Socket<InputStream>) mock(Socket.class))
                    .loadFromResource("private.ks")
                    .storeProtection(mock(PasswordProtection.class))
                    .storeType("JCEKS")
                    .up() // builds the authentication, injects it into the ConsumerLicenseManagerBuilder and returns the latter
                .authentication(mock(Authentication.class))
                .parent() // returns another ConsumerLicenseManagerBuilder
                    //...
                    .up() // builds the consumer license manager, injects it into the child ConsumerLicenseManagerBuilder and returns the latter
                .parent(mock(ConsumerLicenseManager.class))
                .storeIn(mock(Store.class))
                .storeInPath(mock(Path.class))
                .storeInSystemPreferences(ApiDemo.class)
                .storeInUserPreferences(ApiDemo.class)
                .build();
    }
}
