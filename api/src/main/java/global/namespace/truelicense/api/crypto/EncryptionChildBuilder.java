/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.api.crypto;

import global.namespace.truelicense.api.builder.GenBuilder;
import global.namespace.truelicense.api.builder.GenChildBuilder;
import global.namespace.truelicense.api.passwd.PasswordProtection;

/**
 * A child builder for an encryption which injects a password based encryption into some parent builder.
 *
 * @param <ParentBuilder> the type of the parent builder.
 * @author Christian Schlichtherle
 */
public interface EncryptionChildBuilder<ParentBuilder extends GenBuilder<?>> extends GenChildBuilder<ParentBuilder> {

    /**
     * Sets the name of the password based encryption algorithm (optional).
     * If this method is not called, then the name is inherited from some context.
     *
     * @return {@code this}
     */
    EncryptionChildBuilder<ParentBuilder> algorithm(String algorithm);

    /**
     * Sets the password protection which is used for generating a secret key for encryption/decryption.
     *
     * @return {@code this}
     */
    EncryptionChildBuilder<ParentBuilder> protection(PasswordProtection protection);
}
