/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api.crypto;

/**
 * Creates a password based encryption transformation from some given parameters.
 *
 * @author Christian Schlichtherle
 */
public interface EncryptionFactory {

    /** Returns a password based encryption from the given parameters. */
    Encryption encryption(EncryptionParameters encryptionParameters);
}
