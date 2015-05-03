/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

import org.truelicense.api.io.Store;
import org.truelicense.api.misc.Builder;
import org.truelicense.api.misc.Injection;

import java.nio.file.Path;

/**
 * A builder for {@linkplain LicenseConsumerManager license consumer managers}.
 * Call its {@link #build} method to obtain the configured license consumer
 * manager.
 *
 * @param <PasswordSpecification> the generic password specification type.
 * @author Christian Schlichtherle
 */
public interface LicenseConsumerManagerBuilder<PasswordSpecification>
extends Builder<LicenseConsumerManager>,
        Injection<LicenseConsumerManagerBuilder<PasswordSpecification>>,
        LicenseManagerBuilder<PasswordSpecification,
        LicenseConsumerManagerBuilder<PasswordSpecification>> {

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
    LicenseConsumerManagerBuilder<PasswordSpecification> ftpDays(int ftpDays);

    /**
     * Returns a builder for the parent license consumer manager.
     * Call its {@link LicenseConsumerManagerBuilder#inject} method to build and inject
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
    LicenseConsumerManagerBuilder<PasswordSpecification> parent();

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
    LicenseConsumerManagerBuilder<PasswordSpecification> parent(LicenseConsumerManager parent);

    /**
     * Store the license key in the given store.
     * If a non-zero {@linkplain #ftpDays free trial period} (FTP) is
     * configured, then the store will be used for the auto-generated FTP
     * license key and MUST BE KEPT SECRET!
     *
     * @return {@code this}.
     */
    LicenseConsumerManagerBuilder<PasswordSpecification> storeIn(Store store);

    /**
     * Store the license key in the given path.
     * If a non-zero {@linkplain #ftpDays free trial period} (FTP) is
     * configured, then the store will be used for the auto-generated FTP
     * license key and MUST BE KEPT SECRET!
     *
     * @return {@code this}.
     */
    LicenseConsumerManagerBuilder<PasswordSpecification> storeInPath(Path path);

    /**
     * Store the license key in the system preferences node for the package
     * of the given class.
     * If a non-zero {@linkplain #ftpDays free trial period} (FTP) is
     * configured, then the store will be used for the auto-generated FTP
     * license key and MUST BE KEPT SECRET!
     *
     * @return {@code this}.
     */
    LicenseConsumerManagerBuilder<PasswordSpecification> storeInSystemPreferences(Class<?> classInPackage);

    /**
     * Store the license key in the user preferences node for the package
     * of the given class.
     * If a non-zero {@linkplain #ftpDays free trial period} (FTP) is
     * configured, then the store will be used for the auto-generated FTP
     * license key and MUST BE KEPT SECRET!
     *
     * @return {@code this}.
     */
    LicenseConsumerManagerBuilder<PasswordSpecification> storeInUserPreferences(Class<?> classInPackage);
}
