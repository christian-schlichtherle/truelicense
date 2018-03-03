/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.core.crypto;

import global.namespace.fun.io.api.Transformation;
import net.truelicense.api.crypto.EncryptionParameters;
import net.truelicense.api.passwd.Password;
import net.truelicense.api.passwd.PasswordProtection;
import net.truelicense.api.passwd.PasswordUsage;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Objects;

/**
 * A basic password based encryption.
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
public abstract class BasicEncryption implements Transformation {

    private final EncryptionParameters parameters;

    protected BasicEncryption(final EncryptionParameters parameters) {
        this.parameters = Objects.requireNonNull(parameters);
    }

    private PasswordProtection protection() { return parameters.protection(); }

    protected final String algorithm() { return parameters.algorithm(); }

    protected final SecretKey secretKey(final PasswordUsage usage) throws Exception {
        try (Password password = protection().password(usage)) {
            final PBEKeySpec ks = new PBEKeySpec(password.characters());
            try {
                return SecretKeyFactory.getInstance(algorithm()).generateSecret(ks);
            } finally {
                ks.clearPassword();
            }
        }
    }
}
