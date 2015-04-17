/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

import org.truelicense.api.auth.Authentication;
import org.truelicense.api.io.Source;
import org.truelicense.api.misc.Injection;

/**
 * Injects a Key Store Based {@link Authentication} (KSBA) into some target.
 *
 * @param <PasswordSpecification> the generic password specification type.
 * @param <Target> the type of the target.
 * @author Christian Schlichtherle
 */
public interface KsbaInjection<PasswordSpecification, Target>
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
