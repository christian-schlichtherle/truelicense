/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core;

import net.java.truelicense.core.auth.Authentication;
import net.java.truelicense.core.codec.CodecProvider;
import net.java.truelicense.core.crypto.Encryption;
import net.java.truelicense.core.io.Source;
import net.java.truelicense.core.util.Builder;
import net.java.truelicense.core.util.Injection;
import net.java.truelicense.obfuscate.Obfuscate;
import net.java.truelicense.obfuscate.ObfuscatedString;

import javax.annotation.CheckForNull;

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
 * @author Christian Schlichtherle
 */
public interface LicenseVendorContext
extends CodecProvider, LicenseApplicationContext, LicenseProvider {

    /**
     * Configures a license vendor manager.
     *
     * @param  authentication the authentication.
     * @param  encryption the encryption.
     * @return A license vendor manager.
     */
    LicenseVendorManager manager(Authentication authentication, Encryption encryption);

    /**
     * Configures a key store based authentication.
     * <p>
     * The provided strings should be computed on demand from an obfuscated
     * form, e.g. by annotating a constant string value with the
     * &#64;{@link Obfuscate} annotation and processing it with the TrueLicense
     * Maven Plugin.
     *
     * @param source the nullable source for the key store.
     *               May be {@code null} if and only if the key store type
     *               does not require loading from an input stream.
     * @param storeType the nullable type of the key store,
     *                  e.g. {@code "JCEKS"} or {@code "JKS"}.
     *                  If this is {@code null}, then the value computed by the
     *                  expression
     *                  <code>{@link #context()}.{@link LicenseManagementContext#storeType() storeType()}</code>
     *                  is used instead.
     *                  Otherwise, you need to make sure that the store type is
     *                  supported by a security provider on all platforms - see
     *                  <a href="http://docs.oracle.com/javase/6/docs/technotes/guides/security/SunProviders.html">Java Cryptography Architeture Sun Providers Documentation</a>.
     * @param storePassword the password for verifying the integrity of the
     *                      key store.
     * @param alias the alias of the private key entry in the key store.
     * @param keyPassword the nullable password for accessing the private key
     *                    in the key entry.
     *                    If this is {@code null} or empty, then the
     *                    {@code storePassword} is used instead.
     * @return A key store based authentication.
     * @throws IllegalArgumentException if a password is considered to be too
     *         weak.
     */
    Authentication keyStore(@CheckForNull Source source, @CheckForNull String storeType, ObfuscatedString storePassword, String alias, @CheckForNull ObfuscatedString keyPassword);

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException if the password is considered to be too
     *         weak.
     */
    @Override Encryption pbe(String algorithm, ObfuscatedString password);

    /**
     * Returns a builder for
     * {@linkplain LicenseVendorManager license vendor managers}.
     * Call its {@link ManagerBuilder#build} method to obtain a configured
     * license vendor manager.
     */
    ManagerBuilder manager();

    /**
     * A builder for
     * {@linkplain LicenseVendorManager license vendor managers}.
     * Call its {@link #build} method to obtain a configured license vendor
     * manager.
     *
     * @author Christian Schlichtherle
     */
    interface ManagerBuilder extends Builder<LicenseVendorManager> {

        /**
         * Sets the authentication.
         *
         * @return {@code this}.
         */
        ManagerBuilder authentication(Authentication authentication);

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
        KsbaInjection<ManagerBuilder> keyStore();

        /**
         * Sets the encryption.
         *
         * @return {@code this}.
         */
        ManagerBuilder encryption(Encryption encryption);

        /**
         * Returns an injection for a password based encryption (PBE).
         * Call its {@link Injection#inject} method to build and inject the
         * configured encryption into this builder and return it.
         *
         * @see #encryption(Encryption)
         */
        PbeInjection<ManagerBuilder> pbe();
    }
}
