/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.core.crypto;

import global.namespace.truelicense.api.crypto.EncryptionParameters;
import global.namespace.truelicense.api.passwd.Password;
import global.namespace.truelicense.api.passwd.PasswordProtection;
import global.namespace.truelicense.api.passwd.PasswordUsage;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.util.Objects;

/**
 * A mix-in for a password based encryption.
 * This class is immutable.
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
