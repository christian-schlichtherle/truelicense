/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api.auth;

import global.namespace.fun.io.api.Source;
import net.truelicense.api.ConsumerLicenseManager;
import net.truelicense.api.LicenseManagementContextBuilder;
import net.truelicense.api.VendorLicenseManager;
import net.truelicense.api.builder.GenBuilder;
import net.truelicense.api.builder.GenChildBuilder;
import net.truelicense.api.passwd.PasswordProtection;

/**
 * Injects a keystore based authentication into some parent component builder.
 *
 * @param <ParentBuilder> the type of the parent component builder.
 * @author Christian Schlichtherle
 */
public interface AuthenticationBuilder<ParentBuilder extends GenBuilder<?>> extends GenChildBuilder<ParentBuilder> {

    /**
     * Sets the name of the signature algorithm (optional).
     * If this method is not called, then the same algorithm is used which has been used to sign the public key in the
     * entry.
     *
     * @return {@code this}
     */
    AuthenticationBuilder<ParentBuilder> algorithm(String algorithm);

    /**
     * Sets the alias name of the key entry.
     *
     * @return {@code this}
     */
    AuthenticationBuilder<ParentBuilder> alias(String alias);

    /**
     * Sets the password protection which is used for accessing the private key
     * in the key entry (optional).
     * A private key entry is only required to generate license keys, that is
     * for any {@linkplain VendorLicenseManager vendor license manager}
     * and for any
     * {@linkplain ConsumerLicenseManager consumer license manager}
     * for a free trial period.
     * If this method is not called then the
     * {@linkplain #storeProtection(PasswordProtection) keystore protection} is
     * used.
     *
     * @return {@code this}
     */
    AuthenticationBuilder<ParentBuilder> keyProtection(PasswordProtection keyProtection);

    /**
     * Sets the source for loading the keystore (optional).
     * Either this method or {@link #loadFromResource(String)} must be called.
     *
     * @return {@code this}
     * @param source
     */
    AuthenticationBuilder<ParentBuilder> loadFrom(Source source);

    /**
     * Sets the resource name for loading the keystore (optional).
     * Either this method or {@link #loadFrom(Source)} must be called.
     *
     * @return {@code this}
     */
    AuthenticationBuilder<ParentBuilder> loadFromResource(String name);

    /**
     * Sets the password protection which is used for verifying the integrity
     * of the keystore.
     *
     * @return {@code this}
     */
    AuthenticationBuilder<ParentBuilder> storeProtection(PasswordProtection storeProtection);

    /**
     * Sets the type of the keystore (optional).
     * If this method is not called, then the type is inherited from the license management context.
     *
     * @see LicenseManagementContextBuilder#keystoreType(String)
     * @return {@code this}
     */
    AuthenticationBuilder<ParentBuilder> storeType(String storeType);
}
