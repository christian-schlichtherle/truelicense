/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api.crypto;

/**
 * Provides a password based encryption factory.
 *
 * @author Christian Schlichtherle
 */
public interface EncryptionFactoryProvider {

    /** Returns the password based encryption function. */
    EncryptionFactory encryptionFactory();
}
