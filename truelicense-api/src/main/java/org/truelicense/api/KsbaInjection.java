/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

import org.truelicense.api.auth.Authentication;
import org.truelicense.api.io.Source;
import org.truelicense.api.misc.Injection;
import org.truelicense.api.passwd.PasswordProtection;

/**
 * Injects a Key Store Based {@link Authentication} (KSBA) into some target.
 *
 * @param <Target> the type of the target.
 * @author Christian Schlichtherle
 */
public interface KsbaInjection<Target>
extends Injection<Target> {

    /**
     * Sets the algorithm name (optional).
     *
     * @return {@code this}
     */
    KsbaInjection<Target> algorithm(String algorithm);

    /**
     * Sets the alias name of the key entry.
     *
     * @return {@code this}
     */
    KsbaInjection<Target> alias(String alias);

    /**
     * Sets the password protection which is used for accessing the private key
     * in the key entry (optional).
     * A private key entry is only required to generate license keys, that is
     * for any {@linkplain LicenseVendorManager license vendor manager}
     * and for any
     * {@linkplain LicenseConsumerManager license consumer manager}
     * for a free trial period.
     * If this method is not called then the
     * {@linkplain #storeProtection(PasswordProtection) key store protection} is
     * used instead.
     *
     * @return {@code this}
     */
    KsbaInjection<Target> keyProtection(PasswordProtection keyProtection);

    /**
     * Sets the source for the key store (optional).
     *
     * @return {@code this}
     */
    KsbaInjection<Target> loadFrom(Source source);

    /**
     * Sets the resource name of the key store (optional).
     *
     * @return {@code this}
     */
    KsbaInjection<Target> loadFromResource(String name);

    /**
     * Sets the password protection which is used for verifying the integrity
     * of the key store.
     *
     * @return {@code this}
     */
    KsbaInjection<Target> storeProtection(PasswordProtection storeProtection);

    /**
     * Sets the type of the key store,
     * for example {@code "JCEKS"} or {@code "JKS"} (optional).
     *
     * @return {@code this}
     */
    KsbaInjection<Target> storeType(String storeType);
}
