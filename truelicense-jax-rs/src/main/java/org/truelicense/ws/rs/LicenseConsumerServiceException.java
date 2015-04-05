/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.ws.rs;

import java.security.GeneralSecurityException;
import javax.annotation.CheckForNull;
import javax.annotation.concurrent.Immutable;

import org.truelicense.core.LicenseManagementException;

/**
 * Wraps a {@link LicenseManagementException} in order to decorate it with
 * additional meta data for generating an HTTP response.
 *
 * @since  TrueLicense 2.3
 * @author Christian Schlichtherle
 */
@Immutable
public final class LicenseConsumerServiceException
extends GeneralSecurityException {

    private static final long serialVersionUID = 0L;

    private final int status;

    /**
     * Constructs a license consumer service exception with the given HTTP
     * status code and its causing license management exception.
     *
     * @param status the HTTP status code.
     * @param cause the causing license management exception.
     * @see <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html">http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html</a>
     */
    LicenseConsumerServiceException(
            final int status,
            final @CheckForNull LicenseManagementException cause) {
        super(null == cause ? null : cause.getMessage(), cause);
        this.status = status;
    }

    /** Returns the HTTP status code. */
    public int getStatus() { return status; }
}
