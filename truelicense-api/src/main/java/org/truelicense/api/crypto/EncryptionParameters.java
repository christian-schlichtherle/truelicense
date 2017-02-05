/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.crypto;

import org.truelicense.api.passwd.PasswordProtection;

/**
 * Defines parameters for a password based encryption.
 * <p>
 * Unless stated otherwise, all no-argument methods need to return consistent
 * objects so that caching them is not required.
 * A returned object is considered to be consistent if it compares
 * {@linkplain Object#equals(Object) equal} or at least behaves identical to
 * any previously returned object.
 *
 * @author Christian Schlichtherle
 */
public interface EncryptionParameters {

    /** Returns the password based encryption algorithm. */
    String algorithm();

    /**
     * Returns a password protection for generating the secret key for
     * encryption/decryption.
     */
    PasswordProtection protection();
}
