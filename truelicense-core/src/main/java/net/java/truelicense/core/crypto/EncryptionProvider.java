/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core.crypto;

/**
 * Provides an encryption.
 *
 * @author Christian Schlichtherle
 */
public interface EncryptionProvider {

    /** Returns an encryption. */
    Encryption encryption();
}
