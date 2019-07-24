/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.api;

import global.namespace.fun.io.api.Filter;
import global.namespace.truelicense.api.auth.AuthenticationChildBuilder;
import global.namespace.truelicense.api.auth.Authentication;
import global.namespace.truelicense.api.builder.GenChildBuilder;
import global.namespace.truelicense.api.crypto.EncryptionChildBuilder;

/**
 * A mix-in for a builder for license managers.
 *
 * @param <This> the specialized type for fluent programming.
 */
public interface LicenseManagerBuilderMixin<This extends LicenseManagerBuilderMixin<This>> {

    /**
     * Returns an injection for a keystore based authentication.
     * Call its {@link GenChildBuilder#up} method to build and inject the
     * configured authentication into this builder and return it.
     *
     * @see #authentication(Authentication)
     */
    AuthenticationChildBuilder<? extends This> authentication();

    /**
     * Sets the authentication.
     *
     * @return {@code this}.
     */
    This authentication(Authentication authentication);

    /**
     * Returns an injection for a password based encryption.
     * Call its {@link GenChildBuilder#up} method to build and inject the
     * configured encryption into this builder and return it.
     *
     * @see #encryption(Filter)
     */
    EncryptionChildBuilder<? extends This> encryption();

    /**
     * Sets the encryption.
     *
     * @return {@code this}.
     */
    This encryption(Filter encryption);
}
