/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.auth;

import org.truelicense.api.io.Source;
import org.truelicense.api.passwd.PasswordProtection;

import javax.annotation.CheckForNull;

/**
 * Defines parameters for accessing a {@link java.security.KeyStore} which
 * holds the public or private keys to sign or verify a {@link Repository}
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
public interface AuthenticationParameters {

    /**
     * Returns the nullable input source for the key store.
     * May be {@code null} if and only if the key store type does not require
     * loading from an input stream.
     */
    @CheckForNull
    Source source(); // TODO: Return List<Source> instead.

    /**
     * Returns the type of the key store, for example {@code "JCEKS"} or
     * {@code "JKS"}.
     * The returned string should be computed on demand from an obfuscated form,
     * e.g. by processing it with the TrueLicense Maven Plugin.
     */
    String storeType();

    /**
     * Returns a password protection for verifying the integrity of the key
     * store.
     */
    PasswordProtection storeProtection();

    /**
     * Returns the alias of the entry in the key store.
     * The returned string should be computed on demand from an obfuscated form,
     * e.g. by processing it with the TrueLicense Maven Plugin.
     */
    String alias();

    /**
     * Returns a password protection for accessing the private key in the key
     * entry.
     */
    PasswordProtection keyProtection();
}
