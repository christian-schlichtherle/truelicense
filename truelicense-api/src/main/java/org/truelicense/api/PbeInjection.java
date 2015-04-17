/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api;

import org.truelicense.api.crypto.Encryption;
import org.truelicense.api.misc.Injection;

/**
 * Injects a Password Based {@link Encryption} (PBE) into some target.
 *
 * @param <PasswordSpecification> the generic password specification type.
 * @param <Target> the type of the target.
 * @author Christian Schlichtherle
 */
public interface PbeInjection<PasswordSpecification, Target>
extends Injection<Target> {

    /**
     * Sets the algorithm name (optional).
     *
     * @return {@code this}
     */
    PbeInjection<PasswordSpecification, Target> algorithm(String algorithm);

    /**
     * Sets the password for generating a secret key for
     * encryption/decryption.
     *
     * @return {@code this}
     */
    PbeInjection<PasswordSpecification, Target> password(PasswordSpecification password);
}
