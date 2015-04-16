/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core.crypto;

import org.truelicense.api.crypto.Encryption;
import org.truelicense.api.crypto.PbeParameters;
import org.truelicense.api.passwd.Password;
import org.truelicense.api.passwd.PasswordProtection;
import org.truelicense.api.passwd.PasswordUsage;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * A basic Password Based Encryption (PBE).
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
public abstract class BasicPbeEncryption implements Encryption {

    private final PbeParameters parameters;

    protected BasicPbeEncryption(final PbeParameters parameters) {
        this.parameters = Objects.requireNonNull(parameters);
    }

    @Override
    public final PbeParameters parameters() { return parameters; }

    protected final PasswordProtection protection() throws Exception {
        return parameters().protection();
    }

    protected final String algorithm() { return parameters().algorithm(); }

    protected final SecretKey secretKey(final PasswordUsage usage) throws Exception {
        try (Password password = protection().password(usage)) {
            final PBEKeySpec ks = new PBEKeySpec(password.characters());
            try {
                return SecretKeyFactory
                        .getInstance(algorithm())
                        .generateSecret(ks);
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
