/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

import org.truelicense.api.auth.Authentication;
import org.truelicense.api.crypto.Encryption;
import org.truelicense.api.io.Source;
import org.truelicense.api.misc.Injection;

/**
 * A generic builder for license managers.
 *
 * @param <PasswordSpecification> the generic password specification type.
 * @param <This> the specialized type for fluent programming.
 * @author Christian Schlichtherle
 */
public interface LicenseManagerBuilder<
        PasswordSpecification,
        This extends LicenseManagerBuilder<PasswordSpecification, This>> {

    /**
     * Sets the authentication.
     *
     * @return {@code this}.
     */
    This authentication(Authentication authentication);

    /**
     * Returns an injection for a password based encryption (PBE).
     * Call its {@link Injection#inject} method to build and inject the
     * configured encryption into this builder and return it.
     *
     * @see #encryption(Encryption)
     */
    PbeInjection<PasswordSpecification, ? extends This> encryption();

    /**
     * Sets the encryption.
     *
     * @return {@code this}.
     */
    This encryption(Encryption encryption);

    /**
     * Returns an injection for a key store based authentication (KSBA).
     * Call its {@link Injection#inject} method to build and inject the
     * configured authentication into this builder and return it.
     *
     * @see #authentication(Authentication)
     */
    KsbaInjection<PasswordSpecification, ? extends This> keyStore();

    /**
     * Injects a Key Store Based {@link Authentication} (KSBA) into some target.
     */
    interface KsbaInjection<PasswordSpecification, Target>
            extends Injection<Target> {

        /**
         * Sets the algorithm name (optional).
         *
         * @return {@code this}
         */
        KsbaInjection<PasswordSpecification, Target> algorithm(String algorithm);

        /**
         * Sets the alias name of the key entry.
         *
         * @return {@code this}
         */
        KsbaInjection<PasswordSpecification, Target> alias(String alias);

        /**
         * Sets the password for accessing the private key in the key
         * entry (optional).
         * A private key entry is only required to create license keys, that is
         * for any {@linkplain LicenseVendorManager license vendor manager}
         * and for any
         * {@linkplain LicenseConsumerManager license consumer manager}
         * for a free trial period.
         * If this method is not called then the
         * {@linkplain #storePassword(PasswordSpecification) key store
         * password} is used instead.
         *
         * @return {@code this}
         */
        KsbaInjection<PasswordSpecification, Target> keyPassword(PasswordSpecification keyPassword);

        /**
         * Sets the source for the key store (optional).
         *
         * @return {@code this}
         */
        KsbaInjection<PasswordSpecification, Target> loadFrom(Source source);

        /**
         * Sets the resource name of the key store (optional).
         *
         * @return {@code this}
         */
        KsbaInjection<PasswordSpecification, Target> loadFromResource(String name);

        /**
         * Sets the password protection for verifying the integrity of the key
         * store.
         *
         * @return {@code this}
         */
        KsbaInjection<PasswordSpecification, Target> storePassword(PasswordSpecification storePassword);

        /**
         * Sets the type of the key store,
         * for example {@code "JCEKS"} or {@code "JKS"} (optional).
         *
         * @return {@code this}
         */
        KsbaInjection<PasswordSpecification, Target> storeType(String storeType);
    }

    /**
     * Injects a Password Based {@link Encryption} (PBE) into some target.
     */
    interface PbeInjection<PasswordSpecification, Target>
            extends Injection<Target> {

        /**
         * Sets the algorithm name (optional).
         *
         * @return {@code this}
         */
        PbeInjection<PasswordSpecification, Target> algorithm(String algorithm);

        /**
         * Sets the password for generating a secret key for
         * encryption/decryption.
         *
         * @return {@code this}
         */
        PbeInjection<PasswordSpecification, Target> password(PasswordSpecification password);
    }
}
