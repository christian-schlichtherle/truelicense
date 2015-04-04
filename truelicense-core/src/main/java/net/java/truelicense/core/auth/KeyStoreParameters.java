/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core.auth;

import net.java.truelicense.core.io.Source;
import net.java.truelicense.core.io.SourceProvider;
import net.java.truelicense.obfuscate.Obfuscate;

import javax.annotation.CheckForNull;
import java.util.Arrays;

/**
 * Defines parameters for accessing a {@link java.security.KeyStore} which
 * holds the public or private keys to sign or verify a {@link RepositoryModel}
 * by an {@link Authentication}.
 * <p>
 * Unless stated otherwise, all no-argument methods need to return consistent
 * objects so that caching them is not required.
 * A returned object is considered to be consistent if it compares
 * {@linkplain Object#equals(Object) equal} or at least behaves identical to
 * any previously returned object.
 *
 * @author Christian Schlichtherle
 */
public interface KeyStoreParameters
extends AuthenticationParameters, SourceProvider {

    /**
     * Returns the nullable input source for the key store.
     *
     * @return the nullable input source for the key store.
     *         May be {@code null} if and only if the key store type does not
     *         require loading from an input stream.
     */
    @Override @CheckForNull Source source();

    /**
     * Returns the type of the key store,
     * for example {@code "JCEKS"} or {@code "JKS"}.
     * The returned string should be computed on demand from an obfuscated form,
     * e.g. by annotating a constant string value with the \@{@link Obfuscate}
     * annotation and processing it with the TrueLicense Maven Plugin.
     */
    String storeType();

    /**
     * Returns a new char array with the password for verifying the integrity
     * of the key store.
     * <p>
     * It is the caller's responsibility to wipe the contents of the char array
     * after use, e.g. by a call to {@link Arrays#fill(char[], char)}.
     *
     * @return A new char array with the password for verifying the key store.
     */
    char[] storePassword();

    /** Returns the alias of the entry in the key store. */
    String alias();

    /**
     * Returns a new char array with the password for accessing the private key
     * in the key entry.
     * If the returned array is empty, then the
     * {@linkplain #storePassword() key store password} should get used instead.
     * <p>
     * It is the caller's responsibility to wipe the contents of the char array
     * after use, e.g. by a call to {@link Arrays#fill(char[], char)}.
     *
     * @return A new char array with the password for accessing the private key
     *         in the key entry.
     */
    char[] keyPassword();
}
