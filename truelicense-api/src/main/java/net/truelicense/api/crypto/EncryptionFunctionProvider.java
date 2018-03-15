/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api.crypto;

/**
 * Provides an encryption function.
 *
 * @author Christian Schlichtherle
 */
public interface EncryptionFunctionProvider {

    /** Returns the encryption function. */
    EncryptionFunction encryptionFunction();
}
