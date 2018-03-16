/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api.auth;

import global.namespace.fun.io.api.Socket;
import net.truelicense.api.passwd.PasswordProtection;

import java.io.InputStream;
import java.util.Optional;

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
     * If no value is present then the same signature algorithm should be used which was used to sign the public key of
     * the keystore entry which is addressed by the other properties of this interface.
     */
    Optional<String> algorithm();

    /**
     * Returns the alias of the entry in the keystore.
     * The returned string should be computed on demand from an obfuscated form, e.g. by processing it with the
     * TrueLicense Maven Plugin.
     */
    String alias();

    /** Returns a password protection for accessing the private key in the key entry. */
    PasswordProtection keyProtection();

    /**
     * Returns the optional input stream socket for loading the keystore.
     * If no value is present then the keystore type does not require loading from an input source / stream.
     */
    Optional<Socket<InputStream>> source();

    /** Returns a password protection for verifying the integrity of the key store. */
    PasswordProtection storeProtection();

    /**
     * Returns the type of the keystore.
     * The returned string should be computed on demand from an obfuscated form, e.g. by processing it with the
     * TrueLicense Maven Plugin.
     */
    String storeType();
}
