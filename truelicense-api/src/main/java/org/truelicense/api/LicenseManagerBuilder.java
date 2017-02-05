/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

import org.truelicense.api.auth.Authentication;
import org.truelicense.api.io.Transformation;
import org.truelicense.api.misc.Injection;

/**
 * A generic builder for license managers.
 *
 * @param <This> the specialized type for fluent programming.
 * @author Christian Schlichtherle
 */
public interface LicenseManagerBuilder<This extends LicenseManagerBuilder<This>> {

    /**
     * Returns an injection for a key store based authentication.
     * Call its {@link Injection#inject} method to build and inject the
     * configured authentication into this builder and return it.
     *
     * @see #authentication(Authentication)
     */
    AuthenticationInjection<? extends This> authentication();

    /**
     * Sets the authentication.
     *
     * @return {@code this}.
     */
    This authentication(Authentication authentication);

    /**
     * Returns an injection for a password based encryption.
     * Call its {@link Injection#inject} method to build and inject the
     * configured encryption into this builder and return it.
     *
     * @see #encryption(Transformation)
     */
    EncryptionInjection<? extends This> encryption();

    /**
     * Sets the encryption.
     *
     * @return {@code this}.
     */
    This encryption(Transformation encryption);
}
