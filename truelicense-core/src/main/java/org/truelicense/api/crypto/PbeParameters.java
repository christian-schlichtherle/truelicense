/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.crypto;

import org.truelicense.api.passwd.Password;
import org.truelicense.api.passwd.PasswordProtection;

/**
 * Defines parameters for Password Based Encryption (PBE).
 * <p>
 * Unless stated otherwise, all no-argument methods need to return consistent
 * objects so that caching them is not required.
 * A returned object is considered to be consistent if it compares
 * {@linkplain Object#equals(Object) equal} or at least behaves identical to
 * any previously returned object.
 *
 * @author Christian Schlichtherle
 */
public interface PbeParameters extends EncryptionParameters {

    /** Returns the PBE algorithm. */
    String algorithm();

    /**
     * Returns the password protection for generating a secret key for
     * encryption/decryption.
     */
    PasswordProtection protection() throws Exception;
}
