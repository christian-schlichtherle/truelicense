/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.core;

import java.util.Locale;

import net.java.truelicense.core.util.BaseMessage;
import net.java.truelicense.core.util.Message;
import static net.java.truelicense.core.util.Objects.*;

/**
 * Indicates that a
 * {@linkplain LicenseValidation#validate(License) license validation} failed.
 *
 * @author Christian Schlichtherle
 */
public class LicenseValidationException extends LicenseManagementException {

    private static final long serialVersionUID = 1L;

    private final Message msg;

    /**
     * Constructs a license validation exception with the given localized
     * message.
     *
     * @param msg the localized message.
     */
    public LicenseValidationException(final String msg) {
        this(new BaseMessage() {
            static final long serialVersionUID = 0L;

            @Override public String toString(Locale locale) { return msg; }
        });
    }

    /**
     * Constructs a license validation exception with the given
     * internationalized message.
     *
     * @param msg the internationalized message.
     */
    public LicenseValidationException(final Message msg) {
        this.msg = requireNonNull(msg);
    }

    @Override
    public String getMessage() { return msg.toString(Locale.ROOT); }

    @Override
    public String getLocalizedMessage() {
        return msg.toString(Locale.getDefault());
    }

    /**
     * Returns {@code true} if this exception is considered confidential and
     * should not be shared with users.
     * <p>
     * The implementation in the class {@code LicenseValidationException}
     * returns {@code false}.
     */
    @Override public boolean isConsideredConfidential() { return false; }
}
