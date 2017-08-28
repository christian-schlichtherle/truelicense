/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api;

import net.truelicense.api.misc.Injection;
import net.truelicense.api.passwd.PasswordProtection;

/**
 * Injects a password based encryption into some target.
 *
 * @param <Target> the type of the target.
 * @author Christian Schlichtherle
 */
public interface EncryptionInjection<Target>
extends Injection<Target> {

    /**
     * Sets the algorithm name (optional).
     * If this method is not called, then the default algorithm for the license
     * key format is used.
     *
     * @return {@code this}
     */
    EncryptionInjection<Target> algorithm(String algorithm);

    /**
     * Sets the password protection which is used for generating a secret key
     * for encryption/decryption.
     *
     * @return {@code this}
     */
    EncryptionInjection<Target> protection(PasswordProtection protection);
}
