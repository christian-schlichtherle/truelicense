/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core.crypto;

/**
 * Provides an encryption transformation.
 *
 * @author Christian Schlichtherle
 */
public interface EncryptionProvider {
    /** Returns the encryption transformation. */
    Encryption encryption();
}
