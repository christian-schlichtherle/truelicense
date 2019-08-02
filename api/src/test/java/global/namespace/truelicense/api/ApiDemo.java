/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.api;

import global.namespace.fun.io.api.Filter;
import global.namespace.fun.io.api.Source;
import global.namespace.fun.io.api.Store;
import global.namespace.truelicense.api.auth.Authentication;
import global.namespace.truelicense.api.auth.AuthenticationFactory;
import global.namespace.truelicense.api.auth.RepositoryFactory;
import global.namespace.truelicense.api.codec.Codec;
import global.namespace.truelicense.api.crypto.EncryptionFactory;
import global.namespace.truelicense.api.passwd.PasswordPolicy;
import global.namespace.truelicense.api.passwd.PasswordProtection;

import java.nio.file.Path;
import java.time.Clock;

import static org.mockito.Mockito.mock;

/**
 * A demo which uses as many TrueLicense API calls as possible.
 * This is not a working demo - do not use this code in production!
 */
interface ApiDemo {

    LicenseManagementContextBuilder licenseManagementContextBuilder();

    /**
     * Returns a license management context.
     * The returned license management context has been configured using the
     * given license application context and can be used to configure vendor
     * or consumer license managers or create their dependencies, e.g. a store
     * for the license key to generate or install.
     */
    default LicenseManagementContext licenseManagementContext() {
        return licenseManagementContextBuilder()
                .authenticationFactory(mock(AuthenticationFactory.class))
                .authorization(mock(LicenseManagementAuthorization.class))
                .cachePeriodMillis(1000L)
                .codec(mock(Codec.class))
                .clock(mock(Clock.class))
                .compression(mock(Filter.class))
                .encryptionAlgorithm("PBEWithSHA1AndDESede")
                .encryptionFactory(mock(EncryptionFactory.class))
                .initialization(mock(LicenseInitialization.class))
                .initializationComposition(LicenseFunctionComposition.decorate)
                .licenseFactory(mock(LicenseFactory.class))
                .passwordPolicy(mock(PasswordPolicy.class))
                .repositoryFactory(mock(RepositoryFactory.class))
                .subject("MyProduct 1")
                .keystoreType("JCEKS")
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
    default VendorLicenseManager vendorLicenseManager() {
        return licenseManagementContext()
                .vendor() // returns a vendor license manager builder
                .encryption() // returns an encryption builder
                    .algorithm("PBEWithSHA1AndDESede")
                    .protection(mock(PasswordProtection.class))
                    .up() // builds the encryption, injects it into the vendor license manager builder and returns the latter
                .encryption(mock(Filter.class))
                .authentication() // returns an authentication builder
                    .algorithm("RSA")
                    .alias("mykey")
                    .keyProtection(mock(PasswordProtection.class))
                    .loadFrom(mock(Source.class))
                    .loadFromResource("private.ks")
                    .storeProtection(mock(PasswordProtection.class))
                    .storeType("JCEKS")
                    .up() // builds the authentication, injects it into the vendor license manager builder and returns the latter
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
    default ConsumerLicenseManager consumerLicenseManager() {
        return licenseManagementContext()
                .consumer() // returns a consumer license manager builder
                .ftpDays(30)
                .encryption() // returns an encryption builder
                    .algorithm("PBEWithSHA1AndDESede")
                    .protection(mock(PasswordProtection.class))
                    .up() // builds the encryption, injects it into the consumer license manager builder and returns the latter
                .encryption(mock(Filter.class))
                .authentication() // returns an authentication builder
                    .algorithm("RSA")
                    .alias("mykey")
                    .keyProtection(mock(PasswordProtection.class))
                    .loadFrom(mock(Source.class))
                    .loadFromResource("private.ks")
                    .storeProtection(mock(PasswordProtection.class))
                    .storeType("JCEKS")
                    .up() // builds the authentication, injects it into the consumer license manager builder and returns the latter
                .authentication(mock(Authentication.class))
                .parent() // returns another consumer license manager builder
                    //...
                    .up() // builds the consumer license manager, injects it into the child consumer license manager builder and returns the latter
                .parent(mock(ConsumerLicenseManager.class))
                .storeIn(mock(Store.class))
                .storeInPath(mock(Path.class))
                .storeInSystemPreferences(ApiDemo.class)
                .storeInUserPreferences(ApiDemo.class)
                .build();
    }
}
