/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core;

import org.truelicense.core.auth.Authentication;
import org.truelicense.core.crypto.Encryption;
import org.truelicense.core.io.Source;
import org.truelicense.core.io.Store;
import org.truelicense.core.util.Builder;
import org.truelicense.core.util.Injection;
import org.truelicense.obfuscate.Obfuscate;
import org.truelicense.obfuscate.ObfuscatedString;

import javax.annotation.CheckForNull;
import java.io.File;

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
 * @author Christian Schlichtherle
 */
public interface LicenseConsumerContext
extends LicenseApplicationContext {

    /**
     * Configures a license consumer manager
     * for use with regular license keys.
     * <p>
     * The returned manager assumes that it has exclusive access to the given
     * store and can use it to
     * {@linkplain LicenseConsumerManager#install install} and
     * {@linkplain LicenseConsumerManager#uninstall uninstall} a license key.
     *
     * @param  authentication the authentication for regular license keys.
     * @param  encryption the encryption.
     * @param  store the store for the regular license key.
     * @return A license consumer manager for use with regular license keys.
     * @throws IllegalArgumentException if the authentication parameters define
     *         a private key password.
     */
    LicenseConsumerManager manager(Authentication authentication, Encryption encryption, Store store);

    /**
     * Configures a license consumer manager
     * for use with auto-generated free trial period (FTP) license keys.
     * <p>
     * The returned manager assumes that it has exclusive access to the given
     * secret store and can use it to automatically install an FTP license key
     * when calling {@link LicenseConsumerManager#verify} or
     * {@link LicenseConsumerManager#view}.
     * Calls to the methods {@link LicenseConsumerManager#install} and
     * {@link LicenseConsumerManager#uninstall} get forwarded to the parent
     * manager.
     * <p>
     * <strong>
     * NOTE THAT THE SECRET STORE MUST BE HIDDEN OR OTHERWISE PROTECTED SO THAT
     * USERS CANNOT JUST DELETE THE INSTALLED LICENSE KEY TO MAKE THEM ELIGIBLE
     * FOR A NEW FTP AGAIN!
     * </strong>
     *
     * @param  parent the parent manager.
     * @param  authentication the authentication for FTP license keys.
     *         This has to be created with {@link #ftpKeyStore}.
     * @param  encryption the nullable encryption.
     *         If this is {@code null}, then it will get inherited from the
     *         parent manager.
     * @param  secret the secret store for the FTP license key.
     * @param  days the FTP in days (the 24 hour equivalent).
     *         A minimum of one day is required.
     * @return A license consumer manager for use with FTP license keys.
     * @throws IllegalArgumentException if the authentication parameters define
     *         no private key password or if the FTP is less than one day.
     */
    LicenseConsumerManager ftpManager(LicenseConsumerManager parent, Authentication authentication, @CheckForNull Encryption encryption, Store secret, int days);

    /**
     * Configures a chained license consumer manager
     * for use with regular license keys.
     * Use this method to unlock application features based on the purchased
     * license key.
     * For best results, the two managers need to have different authentication
     * parameters.
     * The returned manager should then get used to verify acces to a sub set
     * of the features which are controlled by the parent manager.
     * <p>
     * The returned manager assumes that it has exclusive access to the given
     * store and can use it to
     * {@linkplain LicenseConsumerManager#install install} and
     * {@linkplain LicenseConsumerManager#uninstall uninstall} a license key.
     * <p>
     * All life cycle methods first try to use the parent manager.
     * If this fails for any reason then they try to use the returned manager.
     *
     * @param  parent the parent manager.
     * @param  authentication the authentication for regular license keys.
     * @param  encryption the encryption.
     *         If this is {@code null}, then it will get inherited from the
     *         parent manager.
     * @param  store the secret store for the FTP license key.
     * @return A license consumer manager for use with FTP license keys.
     * @throws IllegalArgumentException if the authentication parameters define
     *         a private key password.
     */
    LicenseConsumerManager chainedManager(LicenseConsumerManager parent, Authentication authentication, @CheckForNull Encryption encryption, Store store);

    /**
     * Configures a key store based authentication
     * for use with regular license keys.
     * <p>
     * The provided strings should be computed on demand from an obfuscated
     * form, e.g. by annotating a constant string value with the
     * &#64;{@link Obfuscate} annotation and processing it with the TrueLicense
     * Maven Plugin.
     *
     * @param source the nullable source for the key store.
     *        May be {@code null} if and only if the key store type does not
     *        require loading from an input stream.
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
     * @param alias the alias of the trusted certificate entry in the key
     *        store.
     * @return A key store based authentication
     *         for use with regular license keys.
     */
    Authentication keyStore(@CheckForNull Source source, @CheckForNull String storeType, ObfuscatedString storePassword, String alias);

    /**
     * Configures a key store based authentication
     * for use with auto-generated free trial period (FTP) license keys.
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
     *                  If this is {@code null}, then the value computed by
     *                  the expression
     *                  <code>{@link #context()}.{@link LicenseManagementContext#storeType() storeType()}</code>
     *                  is used instead.
     *                  Otherwise, you need to make sure that the store type
     *                  is supported by a security provider on all platforms -
     *                  see
     *                  <a href="http://docs.oracle.com/javase/6/docs/technotes/guides/security/SunProviders.html">Java Cryptography Architeture Sun Providers Documentation</a>.
     * @param storePassword the password for verifying the integrity of the
     *                      key store.
     * @param alias the alias of the private key entry in the key store.
     * @param keyPassword the nullable password for accessing the private key
     *                    in the key entry.
     *                    If this is {@code null} or empty, then the
     *                    {@code storePassword} is used instead.
     * @return A key store based authentication
     *         for use with FTP license keys.
     * @throws IllegalArgumentException if a password is considered to be too
     *         weak.
     */
    Authentication ftpKeyStore(@CheckForNull Source source, @CheckForNull String storeType, ObfuscatedString storePassword, String alias, @CheckForNull ObfuscatedString keyPassword);

    /**
     * Returns a builder for
     * {@linkplain LicenseConsumerManager license consumer managers}.
     * Call its {@link ManagerBuilder#build} method to obtain a configured
     * license consumer manager.
     */
    ManagerBuilder manager();

    /**
     * A builder for
     * {@linkplain LicenseConsumerManager license consumer managers}.
     * Call {@link #build} to obtain a configured license consumer manager.
     *
     * @author Christian Schlichtherle
     */
    interface ManagerBuilder
    extends Builder<LicenseConsumerManager>, Injection<ManagerBuilder> {

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
        ManagerBuilder parent(LicenseConsumerManager parent);

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
        ManagerBuilder parent();

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
        ManagerBuilder ftpDays(int ftpDays);

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
         * The keystore needs to have a key password configured if and only if
         * the license consumer manager to build defines a non-zero
         * {@linkplain #ftpDays free trial period} (FTP).
         *
         * @see #authentication(Authentication)
         */
        KsbaInjection<ManagerBuilder> keyStore();

        /**
         * Sets the encryption.
         * An encryption needs to be configured if no
         * {@linkplain #parent parent license consumer manager} is configured.
         * Otherwise, the encryption gets inherited from the parent license
         * consumer manager.
         *
         * @return {@code this}.
         */
        ManagerBuilder encryption(Encryption encryption);

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
        PbeInjection<ManagerBuilder> pbe();

        /**
         * Stores the license key in the given store.
         * If a non-zero {@linkplain #ftpDays free trial period} (FTP) is
         * configured, then the store will be used for the auto-generated FTP
         * license keys and MUST BE KEPT SECRET!
         *
         * @return {@code this}.
         */
        ManagerBuilder storeIn(Store store);

        /**
         * Stores the license key in the given file.
         * If a non-zero {@linkplain #ftpDays free trial period} (FTP) is
         * configured, then the store will be used for the auto-generated FTP
         * license keys and MUST BE KEPT SECRET!
         *
         * @return {@code this}.
         */
        ManagerBuilder storeInFile(File file);

        /**
         * Stores the license key in the system preferences node for the
         * package of the given class.
         * If a non-zero {@linkplain #ftpDays free trial period} (FTP) is
         * configured, then the store will be used for the auto-generated FTP
         * license keys and MUST BE KEPT SECRET!
         *
         * @return {@code this}.
         */
        ManagerBuilder storeInSystemNode(Class<?> classInPackage);

        /**
         * Stores the license keys in the user preferences node for the
         * package of the given class.
         * If a non-zero {@linkplain #ftpDays free trial period} (FTP) is
         * configured, then the store will be used for the auto-generated FTP
         * license keys and MUST BE KEPT SECRET!
         *
         * @return {@code this}.
         */
        ManagerBuilder storeInUserNode(Class<?> classInPackage);
    }
}
