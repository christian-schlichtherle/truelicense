/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

import org.truelicense.api.auth.Authentication;
import org.truelicense.api.crypto.Encryption;
import org.truelicense.api.io.Store;
import org.truelicense.api.misc.Builder;
import org.truelicense.api.misc.Injection;

import java.nio.file.Path;

/**
 * A derived context for license consumer applications.
 * Use this context to configure a {@link LicenseConsumerManager} with the
 * required parameters.
 * For a demonstration of this API, please use the TrueLicense Maven Archetype
 * to generate a sample project - even if you don't use Maven to build your
 * software product.
 * <p>
 * Applications have no need to implement this interface and should not do so
 * because it may be subject to expansion in future versions.
 *
 * @param <PasswordSpecification> the generic password specification type.
 * @author Christian Schlichtherle
 */
public interface LicenseConsumerContext<PasswordSpecification>
extends LicenseApplicationContext {

    /**
     * Returns a builder for a
     * {@linkplain LicenseConsumerManager license consumer manager}.
     * Call its {@link ManagerBuilder#build} method to obtain a configured
     * license consumer manager.
     */
    ManagerBuilder<PasswordSpecification> manager();

    /**
     * A builder for
     * {@linkplain LicenseConsumerManager license consumer managers}.
     * Call {@link #build} to obtain a configured license consumer manager.
     *
     * @author Christian Schlichtherle
     */
    interface ManagerBuilder<PasswordSpecification>
    extends Builder<LicenseConsumerManager>, Injection<ManagerBuilder<PasswordSpecification>> {

        /**
         * Sets the authentication.
         *
         * @return {@code this}.
         */
        ManagerBuilder<PasswordSpecification> authentication(Authentication authentication);

        /**
         * Returns an injection for a password based encryption (PBE).
         * Call its {@link Injection#inject} method to build and inject the
         * configured encryption into this builder and return it.
         * <p>
         * PBE parameters need to be configured if no
         * {@linkplain #parent parent license consumer manager} is configured.
         * Otherwise, the PBE parameters get inherited from the parent license
         * consumer manager.
         *
         * @see #encryption(Encryption)
         */
        PbeInjection<? extends ManagerBuilder<PasswordSpecification>, PasswordSpecification> encryption();

        /**
         * Sets the encryption.
         * An encryption needs to be configured if no
         * {@linkplain #parent parent license consumer manager} is configured.
         * Otherwise, the encryption gets inherited from the parent license
         * consumer manager.
         *
         * @return {@code this}.
         */
        ManagerBuilder<PasswordSpecification> encryption(Encryption encryption);

        /**
         * Sets the free trial period (FTP) in days (the 24 hour equivalent).
         * If this is zero, then no FTP is configured.
         * Otherwise, the {@linkplain #keyStore key store} needs to have a
         * password configured for the private key entry and a
         * {@linkplain #parent parent license consumer manager}
         * needs to be configured for the regular license keys.
         *
         * @return {@code this}.
         */
        ManagerBuilder<PasswordSpecification> ftpDays(int ftpDays);

        /**
         * Returns an injection for a key store based authentication (KSBA).
         * Call its {@link Injection#inject} method to build and inject the
         * configured authentication into this builder and return it.
         *
         * @see #authentication(Authentication)
         */
        KsbaInjection<? extends ManagerBuilder<PasswordSpecification>, PasswordSpecification> keyStore();

        /**
         * Returns a builder for the parent license consumer manager.
         * Call its {@link ManagerBuilder#inject} method to build and inject
         * the configured parent license consumer manager into this builder and
         * return it.
         * <p>
         * A parent license consumer manager is required to configure a
         * non-zero {@linkplain #ftpDays free trial period} (FTP).
         * The parent license consumer manager will be tried first whenever a
         * {@linkplain LicenseConsumerManager life cycle management method}
         * is executed, e.g. when verifying a license key.
         *
         * @see #parent(LicenseConsumerManager)
         */
        ManagerBuilder<PasswordSpecification> parent();

        /**
         * Sets the parent license consumer manager.
         * A parent license consumer manager is required to configure a
         * non-zero {@linkplain #ftpDays free trial period} (FTP).
         * The parent license consumer manager will be tried first whenever a
         * {@linkplain LicenseConsumerManager life cycle management method}
         * is executed, e.g. when verifying a license key.
         *
         * @return {@code this}.
         */
        ManagerBuilder<PasswordSpecification> parent(LicenseConsumerManager parent);

        /**
         * Store the license key in the given store.
         * If a non-zero {@linkplain #ftpDays free trial period} (FTP) is
         * configured, then the store will be used for the auto-generated FTP
         * license key and MUST BE KEPT SECRET!
         *
         * @return {@code this}.
         */
        ManagerBuilder<PasswordSpecification> storeIn(Store store);

        /**
         * Store the license key in the given path.
         * If a non-zero {@linkplain #ftpDays free trial period} (FTP) is
         * configured, then the store will be used for the auto-generated FTP
         * license key and MUST BE KEPT SECRET!
         *
         * @return {@code this}.
         */
        ManagerBuilder<PasswordSpecification> storeInPath(Path path);

        /**
         * Store the license key in the system preferences node for the
         * package of the given class.
         * If a non-zero {@linkplain #ftpDays free trial period} (FTP) is
         * configured, then the store will be used for the auto-generated FTP
         * license key and MUST BE KEPT SECRET!
         *
         * @return {@code this}.
         */
        ManagerBuilder<PasswordSpecification> storeInSystemPreferences(Class<?> classInPackage);

        /**
         * Store the license key in the user preferences node for the
         * package of the given class.
         * If a non-zero {@linkplain #ftpDays free trial period} (FTP) is
         * configured, then the store will be used for the auto-generated FTP
         * license key and MUST BE KEPT SECRET!
         *
         * @return {@code this}.
         */
        ManagerBuilder<PasswordSpecification> storeInUserPreferences(Class<?> classInPackage);
    }
}
