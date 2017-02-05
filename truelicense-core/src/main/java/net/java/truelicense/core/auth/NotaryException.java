/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core.auth;

import net.java.truelicense.core.util.Message;

import java.security.GeneralSecurityException;
import java.util.Locale;

import static net.java.truelicense.core.util.Objects.requireNonNull;

/**
 * Indicates that a notary could not access the private or public key in a
 * key store due to insufficient or incorrect configuration parameters.
 *
 * @author Christian Schlichtherle
 */
public class NotaryException extends GeneralSecurityException {

    private static final long serialVersionUID = 1L;

    private final Message msg;

    /**
     * Constructs a notary exception with the given message.
     *
     * @param msg the message.
     */
    public NotaryException(final Message msg) {
        this.msg = requireNonNull(msg);
    }

    @Override
    public String getMessage() { return msg.toString(Locale.ROOT); }

    @Override
    public String getLocalizedMessage() { return msg.toString(); }
}
