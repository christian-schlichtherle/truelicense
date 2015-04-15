/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.jax.rs;

import org.truelicense.api.LicenseManagementException;

import java.security.GeneralSecurityException;

/**
 * Wraps a {@link LicenseManagementException} in order to decorate it with
 * additional meta data for generating an HTTP response.
 * This class is immutable.
 *
 * @since  TrueLicense 2.3
 * @author Christian Schlichtherle
 */
public final class LicenseConsumerServiceException
extends GeneralSecurityException {

    private static final long serialVersionUID = 0L;

    private final int status;

    /**
     * Constructs a license consumer service exception with the given HTTP
     * status code and its causing license management exception.
     *
     * @param status the HTTP status code.
     * @param nullableCause the nullable causing license management exception.
     * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html">http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html</a>
     */
    LicenseConsumerServiceException(
            final int status,
            final LicenseManagementException nullableCause) {
        super(null == nullableCause ? null : nullableCause.getMessage(), nullableCause);
        this.status = status;
    }

    /** Returns the HTTP status code. */
    public int getStatus() { return status; }
}
