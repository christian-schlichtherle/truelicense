/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.auth;

import org.truelicense.api.io.Source;
import org.truelicense.api.passwd.PasswordProtection;

import java.util.List;

/**
 * Defines parameters for accessing a {@link java.security.KeyStore} which
 * holds the public or private keys to sign or verify an artifact using an
 * {@link Authentication}.
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
     * Returns the optional signature algorithm.
     * This is a list of at most one non-null item.
     * The list may be empty to indicate that the same signature algorithm
     * should be used which was used to sign the public key of the key store
     * entry which is addressed by the other properties of this interface.
     */
    List<String> algorithm();

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

    /**
     * Returns the optional input source for the key store.
     * This is a list of at most one non-null item.
     * The list may be empty to indicate that the key store type does not
     * require loading from an input source / stream.
     */
    List<Source> source();

    /**
     * Returns a password protection for verifying the integrity of the key
     * store.
     */
    PasswordProtection storeProtection();

    /**
     * Returns the type of the key store, for example {@code "JCEKS"} or
     * {@code "JKS"}.
     * The returned string should be computed on demand from an obfuscated form,
     * e.g. by processing it with the TrueLicense Maven Plugin.
     */
    String storeType();
}
