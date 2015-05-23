/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

import org.truelicense.api.io.Source;
import org.truelicense.api.misc.Injection;
import org.truelicense.api.passwd.PasswordProtection;

/**
 * Injects a key store based authentication into some target.
 *
 * @param <Target> the type of the target.
 * @author Christian Schlichtherle
 */
public interface AuthenticationInjection<Target>
extends Injection<Target> {

    /**
     * Sets the name of the signature algorithm (optional).
     * If this method is not called, then the same algorithm is used which has
     * been used to sign the public key in the entry.
     *
     * @return {@code this}
     */
    AuthenticationInjection<Target> algorithm(String algorithm);

    /**
     * Sets the alias name of the key entry.
     *
     * @return {@code this}
     */
    AuthenticationInjection<Target> alias(String alias);

    /**
     * Sets the password protection which is used for accessing the private key
     * in the key entry (optional).
     * A private key entry is only required to generate license keys, that is
     * for any {@linkplain VendorLicenseManager vendor license manager}
     * and for any
     * {@linkplain ConsumerLicenseManager consumer license manager}
     * for a free trial period.
     * If this method is not called then the
     * {@linkplain #storeProtection(PasswordProtection) key store protection} is
     * used.
     *
     * @return {@code this}
     */
    AuthenticationInjection<Target> keyProtection(PasswordProtection keyProtection);

    /**
     * Sets the source for the key store (optional).
     * Either this method or {@link #loadFromResource(String)} must be called.
     *
     * @return {@code this}
     */
    AuthenticationInjection<Target> loadFrom(Source source);

    /**
     * Sets the resource name of the key store (optional).
     * Either this method or {@link #loadFrom(Source)} must be called.
     *
     * @return {@code this}
     */
    AuthenticationInjection<Target> loadFromResource(String name);

    /**
     * Sets the password protection which is used for verifying the integrity
     * of the key store.
     *
     * @return {@code this}
     */
    AuthenticationInjection<Target> storeProtection(PasswordProtection storeProtection);

    /**
     * Sets the type of the key store,
     * for example {@code "JCEKS"} or {@code "JKS"} (optional).
     *
     * @return {@code this}
     */
    AuthenticationInjection<Target> storeType(String storeType);
}
