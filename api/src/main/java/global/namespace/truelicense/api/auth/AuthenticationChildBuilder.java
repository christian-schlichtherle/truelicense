/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.api.auth;

import global.namespace.fun.io.api.Source;
import global.namespace.truelicense.api.ConsumerLicenseManager;
import global.namespace.truelicense.api.VendorLicenseManager;
import global.namespace.truelicense.api.builder.GenBuilder;
import global.namespace.truelicense.api.builder.GenChildBuilder;
import global.namespace.truelicense.api.passwd.PasswordProtection;

/**
 * A child builder for an authentication which injects a keystore based authentication into some parent builder.
 *
 * @param <ParentBuilder> the type of the parent builder.
 */
public interface AuthenticationChildBuilder<ParentBuilder extends GenBuilder<?>> extends GenChildBuilder<ParentBuilder> {

    /**
     * Sets the name of the signature algorithm (optional).
     * If this method is not called, then the same algorithm is used which has been used to sign the public key in the
     * entry.
     *
     * @return {@code this}
     */
    AuthenticationChildBuilder<ParentBuilder> algorithm(String algorithm);

    /**
     * Sets the alias name of the key entry.
     *
     * @return {@code this}
     */
    AuthenticationChildBuilder<ParentBuilder> alias(String alias);

    /**
     * Sets the password protection which is used for accessing the private key in the key entry (optional).
     * A private key entry is only required to generate license keys, that is for any
     * {@linkplain VendorLicenseManager vendor license manager} and for any
     * {@linkplain ConsumerLicenseManager consumer license manager} for a free trial period.
     * If this method is not called then the {@linkplain #storeProtection(PasswordProtection) keystore protection} is
     * used.
     *
     * @return {@code this}
     */
    AuthenticationChildBuilder<ParentBuilder> keyProtection(PasswordProtection keyProtection);

    /**
     * Sets the source for loading the keystore (optional).
     * Either this method or {@link #loadFromResource(String)} must be called.
     *
     * @param source
     * @return {@code this}
     */
    AuthenticationChildBuilder<ParentBuilder> loadFrom(Source source);

    /**
     * Sets the resource name for loading the keystore (optional).
     * Either this method or {@link #loadFrom(Source)} must be called.
     *
     * @return {@code this}
     */
    AuthenticationChildBuilder<ParentBuilder> loadFromResource(String name);

    /**
     * Sets the password protection which is used for verifying the integrity of the keystore.
     *
     * @return {@code this}
     */
    AuthenticationChildBuilder<ParentBuilder> storeProtection(PasswordProtection storeProtection);

    /**
     * Sets the type of the keystore (optional).
     * If this method is not called, then the type is inherited from the builder's context.
     *
     * @return {@code this}
     */
    AuthenticationChildBuilder<ParentBuilder> storeType(String storeType);
}
