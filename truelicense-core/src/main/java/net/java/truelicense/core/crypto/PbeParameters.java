/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core.crypto;

import java.util.Arrays;

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
     * Returns a new char array with the password used to generate a secret key
     * for encryption/decryption.
     * <p>
     * It is the caller's responsibility to wipe the contents of the char array
     * after use, e.g. by a call to {@link Arrays#fill(char[], char)}.
     *
     * @return A new char array with the password used to generate a secret key
     *         for encryption/decryption.
     */
    char[] password();
}
