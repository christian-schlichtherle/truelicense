/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.core.crypto;

import org.truelicense.api.crypto.Encryption;
import org.truelicense.api.crypto.PbeParameters;

import javax.annotation.concurrent.Immutable;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * A basic Password Based Encryption (PBE).
 *
 * @author Christian Schlichtherle
 */
@Immutable
public abstract class BasicPbeEncryption implements Encryption {

    private final PbeParameters pbe;

    protected BasicPbeEncryption(final PbeParameters pbe) {
        this.pbe = Objects.requireNonNull(pbe);
    }

    @Override public final PbeParameters parameters() { return pbe; }

    protected final char[] password() { return parameters().password(); }

    protected final String algorithm() { return parameters().algorithm(); }

    protected final SecretKey secretKey() throws Exception {
        final SecretKeyFactory skf = SecretKeyFactory.getInstance(algorithm());
        final PBEKeySpec ks = new PBEKeySpec(password());
        try { return skf.generateSecret(ks); }
        finally { ks.clearPassword(); }
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
        catch (final RuntimeException ex) { throw ex; }
        catch (final IOException ex) { throw ex; }
        catch (final Exception ex) { throw new IOException(ex); } // TODO: Make this a Throwable with Java 7
    }
}
