/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core;

import net.java.truelicense.core.auth.Authentication;
import net.java.truelicense.core.crypto.Encryption;
import net.java.truelicense.core.io.Source;
import net.java.truelicense.core.io.Store;
import net.java.truelicense.core.util.ContextProvider;
import net.java.truelicense.core.util.Injection;
import net.java.truelicense.obfuscate.Obfuscate;
import net.java.truelicense.obfuscate.ObfuscatedString;

import javax.annotation.CheckForNull;
import java.io.File;

/**
 * A context which has been derived from a
 * {@linkplain LicenseManagementContext license management context}.
 * <p>
 * Applications have no need to implement this interface and should not do so
 * because it may be subject to expansion in future versions.
 *
 * @author Christian Schlichtherle
 */
public interface LicenseApplicationContext
extends ContextProvider<LicenseManagementContext> {

    /**
     * Configures a Password Based Encryption (PBE).
     * <p>
     * If the given {@code algorithm} name is {@code null}, then it defaults to
     * the value computed by the expression
     * <code>{@link #context()}.{@link LicenseManagementContext#pbeAlgorithm() pbeAlgorithm()}</code>.
     * Otherwise, you need to make sure that the named algorithm is supported
     * by a security provider on all platforms - see
     * <a href="http://docs.oracle.com/javase/6/docs/technotes/guides/security/SunProviders.html">Java Cryptography Architeture Sun Providers Documentation</a>.
     * <p>
     * Note that the configured license key format may define additional
     * constraints.
     * For example, applications which require compatibility with V1 format
     * license keys must use the {@code PBEWithMD5AndDES} algorithm, so they
     * should provide {@code null}.
     * <p>
     * If an algorithm name is specified, the string should be computed on
     * demand from an obfuscated form, e.g. by annotating a constant string
     * value with the &#64;{@link Obfuscate} annotation and processing it with
     * the TrueLicense Maven Plugin.
     *
     * @param  algorithm the nullable PBE algorithm name.
     * @param  password the password.
     * @return A Password Based Encryption (PBE).
     */
    Encryption pbe(@CheckForNull String algorithm, ObfuscatedString password);

    /**
     * Returns a source which loads the resource with the given {@code name}.
     * The provided string should be computed on demand from an obfuscated form,
     * e.g. by annotating a constant string value with the &#64;{@link Obfuscate}
     * annotation and processing it with the TrueLicense Maven Plugin.
     * <p>
     * The resource will get loaded using the class loader as defined by the
     * root license management context.
     *
     * @param  name the name of the resource to load.
     * @return A source which loads the resource with the given {@code name}.
     */
    Source resource(String name);

    /** Returns a store for the given {@code file}. */
    Store fileStore(File file);

    /**
     * Returns a store for the system preferences node for the package of the
     * given class.
     * Note that the class should be exempt from byte code obfuscation or
     * otherwise you might use an unintended store location and risk a
     * collision with third party software.
     */
    Store systemNodeStore(Class<?> classInPackage);

    /**
     * Returns a store for the user preferences node for the package of the
     * given class.
     * Note that the class should be exempt from byte code obfuscation or
     * otherwise you might use an unintended store location and risk a
     * collision with third party software.
     */
    Store userNodeStore(Class<?> classInPackage);

    /**
     * Injects a Key Store Based {@link Authentication} (KSBA)
     * into some target.
     */
    interface KsbaInjection<Target> extends Injection<Target> {

        /**
         * Sets the source for the key store.
         *
         * @return {@code this}
         */
        KsbaInjection<Target> loadFrom(Source source);

        /**
         * Sets the resource name of the key store.
         *
         * @return {@code this}
         */
        KsbaInjection<Target> loadFromResource(String name);

        /**
         * Sets the type of the key store,
         * for example {@code "JCEKS"} or {@code "JKS"}.
         *
         * @return {@code this}
         * @see    LicenseConsumerContext#keyStore
         * @see    LicenseConsumerContext#ftpKeyStore
         * @see    LicenseVendorContext#keyStore
         */
        KsbaInjection<Target> storeType(@CheckForNull String storeType);

        /**
         * Sets the password for verifying the integrity of the key store.
         *
         * @return {@code this}
         */
        KsbaInjection<Target> storePassword(ObfuscatedString storePassword);

        /**
         * Sets the alias name of the key entry.
         *
         * @return {@code this}
         */
        KsbaInjection<Target> alias(String alias);

        /**
         * Sets the password for accessing the private key in the key entry.
         * A private key entry is only required to create license keys, that is
         * for any {@linkplain LicenseVendorManager license vendor manager}
         * and for any
         * {@linkplain LicenseConsumerManager license consumer manager}
         * for a free trial period.
         * If this method is not called or if an empty password is passed, then
         * the {@linkplain #storePassword(ObfuscatedString) key store password}
         * is used instead.
         *
         * @return {@code this}
         */
        KsbaInjection<Target> keyPassword(ObfuscatedString keyPassword);
    }

    /**
     * Injects a Password Based {@link Encryption} (PBE)
     * into some target.
     */
    interface PbeInjection<Target> extends Injection<Target> {

        /**
         * Sets the algorithm name.
         *
         * @return {@code this}
         * @see LicenseApplicationContext#pbe
         */
        PbeInjection<Target> algorithm(@CheckForNull String algorithm);

        /**
         * Sets the password used to generate a secret key for
         * encryption/decryption.
         *
         * @return {@code this}
         * @see    LicenseApplicationContext#pbe
         */
        PbeInjection<Target> password(ObfuscatedString password);
    }
}
