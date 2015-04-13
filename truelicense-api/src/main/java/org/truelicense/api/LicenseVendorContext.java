/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

import org.truelicense.api.auth.Authentication;
import org.truelicense.api.codec.CodecProvider;
import org.truelicense.api.crypto.Encryption;
import org.truelicense.api.misc.Builder;
import org.truelicense.api.misc.Injection;

import javax.annotation.ParametersAreNullableByDefault;

/**
 * A derived context for license vendor applications alias license key tools.
 * Use this context to configure a {@link LicenseVendorManager} with the
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
public interface LicenseVendorContext<PasswordSpecification>
extends CodecProvider,
        LicenseApplicationContext<PasswordSpecification>,
        LicenseProvider {

    /**
     * Returns a builder for a
     * {@linkplain LicenseVendorManager license vendor manager}.
     * Call its {@link ManagerBuilder#build} method to obtain a configured
     * license vendor manager.
     */
    ManagerBuilder<PasswordSpecification> manager();

    /**
     * A builder for
     * {@linkplain LicenseVendorManager license vendor managers}.
     * Call its {@link #build} method to obtain a configured license vendor
     * manager.
     *
     * @author Christian Schlichtherle
     */
    @ParametersAreNullableByDefault
    interface ManagerBuilder<PasswordSpecification> extends Builder<LicenseVendorManager> {

        /**
         * Sets the authentication.
         *
         * @return {@code this}.
         */
        ManagerBuilder<PasswordSpecification> authentication(Authentication authentication);

        /**
         * Returns an injection for a key store based authentication.
         * Call its {@link Injection#inject} method to build and inject the
         * configured authentication into this builder and return it.
         * <p>
         * The keystore needs to have a key password configured for the private
         * key entry.
         *
         * @see #authentication(Authentication)
         */
        KsbaInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> keyStore();

        /**
         * Sets the encryption.
         *
         * @return {@code this}.
         */
        ManagerBuilder<PasswordSpecification> encryption(Encryption encryption);

        /**
         * Returns an injection for a password based encryption.
         * Call its {@link Injection#inject} method to build and inject the
         * configured encryption into this builder and return it.
         *
         * @see #encryption(Encryption)
         */
        PbeInjection<ManagerBuilder<PasswordSpecification>, PasswordSpecification> encryption();
    }
}
