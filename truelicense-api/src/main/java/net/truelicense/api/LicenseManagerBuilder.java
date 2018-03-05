/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api;

import global.namespace.fun.io.api.Transformation;
import net.truelicense.api.auth.Authentication;
import net.truelicense.api.misc.ChildBuilder;

/**
 * A generic builder for license managers.
 *
 * @param <This> the specialized type for fluent programming.
 * @author Christian Schlichtherle
 */
public interface LicenseManagerBuilder<This extends LicenseManagerBuilder<This>> {

    /**
     * Returns an injection for a key store based authentication.
     * Call its {@link ChildBuilder#up} method to build and inject the
     * configured authentication into this builder and return it.
     *
     * @see #authentication(Authentication)
     */
    AuthenticationBuilder<? extends This> authentication();

    /**
     * Sets the authentication.
     *
     * @return {@code this}.
     */
    This authentication(Authentication authentication);

    /**
     * Returns an injection for a password based encryption.
     * Call its {@link ChildBuilder#up} method to build and inject the
     * configured encryption into this builder and return it.
     *
     * @see #encryption(Transformation)
     */
    EncryptionBuilder<? extends This> encryption();

    /**
     * Sets the encryption.
     *
     * @return {@code this}.
     */
    This encryption(Transformation encryption);
}
