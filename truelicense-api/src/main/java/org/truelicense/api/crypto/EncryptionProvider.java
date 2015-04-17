/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.crypto;

/**
 * Provides an encryption.
 *
 * @author Christian Schlichtherle
 */
public interface EncryptionProvider {

    /** Returns the encryption. */
    Encryption encryption();
}
