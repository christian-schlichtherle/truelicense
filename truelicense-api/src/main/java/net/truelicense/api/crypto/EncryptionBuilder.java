/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api.crypto;

import net.truelicense.api.LicenseManagementContextBuilder;
import net.truelicense.api.builder.GenBuilder;
import net.truelicense.api.builder.GenChildBuilder;
import net.truelicense.api.passwd.PasswordProtection;

/**
 * Injects a password based encryption into some parent component builder.
 *
 * @param <ParentBuilder> the type of the parent component builder.
 * @author Christian Schlichtherle
 */
public interface EncryptionBuilder<ParentBuilder extends GenBuilder<?>> extends GenChildBuilder<ParentBuilder> {

    /**
     * Sets the name of the password based encryption algorithm (optional).
     * If this method is not called, then the name is inherited from the license management context.
     *
     * @see LicenseManagementContextBuilder#encryptionAlgorithm(String)
     * @return {@code this}
     */
    EncryptionBuilder<ParentBuilder> algorithm(String algorithm);

    /**
     * Sets the password protection which is used for generating a secret key for encryption/decryption.
     *
     * @return {@code this}
     */
    EncryptionBuilder<ParentBuilder> protection(PasswordProtection protection);
}
