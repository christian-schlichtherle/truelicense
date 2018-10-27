/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.truelicense.api;

import global.namespace.fun.io.api.Filter;
import net.truelicense.api.auth.Authentication;
import net.truelicense.api.auth.AuthenticationChildBuilder;
import net.truelicense.api.builder.GenChildBuilder;
import net.truelicense.api.crypto.EncryptionChildBuilder;

/**
 * A fragment of a builder for license managers.
 *
 * @param <This> the specialized type for fluent programming.
 * @author Christian Schlichtherle
 */
public interface LicenseManagerBuilderFragment<This extends LicenseManagerBuilderFragment<This>> {

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
