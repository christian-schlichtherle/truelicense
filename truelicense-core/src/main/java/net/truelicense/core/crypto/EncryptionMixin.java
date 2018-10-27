/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.truelicense.core.crypto;

import net.truelicense.api.crypto.EncryptionParameters;
import net.truelicense.api.passwd.Password;
import net.truelicense.api.passwd.PasswordProtection;
import net.truelicense.api.passwd.PasswordUsage;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Objects;

/**
 * A mix-in for a password based encryption.
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
public abstract class EncryptionMixin {

    private final EncryptionParameters parameters;

    protected EncryptionMixin(final EncryptionParameters parameters) {
        this.parameters = Objects.requireNonNull(parameters);
    }

    private PasswordProtection passwordProtection() {
        return parameters.protection();
    }

    protected final String algorithm() {
        return parameters.algorithm();
    }

    protected final SecretKey secretKey(final PasswordUsage usage) throws Exception {
        try (Password password = passwordProtection().password(usage)) {
            final PBEKeySpec ks = new PBEKeySpec(password.characters());
            try {
                return SecretKeyFactory.getInstance(algorithm()).generateSecret(ks);
            } finally {
                ks.clearPassword();
            }
        }
    }
}
