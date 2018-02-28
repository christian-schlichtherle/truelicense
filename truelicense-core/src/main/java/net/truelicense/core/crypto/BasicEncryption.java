/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.core.crypto;

import net.truelicense.api.crypto.EncryptionParameters;
import net.truelicense.api.io.Transformation;
import net.truelicense.api.passwd.Password;
import net.truelicense.api.passwd.PasswordProtection;
import net.truelicense.api.passwd.PasswordUsage;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Callable;

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

    /**
     * Executes the given {@code task} and wraps any
     * non-{@link RuntimeException} and non-{@link IOException}
     * in a new {@code IOException}.
     *
     * @param  task the task to {@link Callable#call}.
     * @return the result of calling the task.
     * @throws RuntimeException at the discretion of the task.
     * @throws IOException on any other {@link Exception} thrown by the task.
     */
    protected static <V> V wrap(final Callable<V> task) throws IOException {
        try { return task.call(); }
        catch (RuntimeException | IOException e) { throw e; }
        catch (Exception e) { throw new IOException(e); }
    }
}
