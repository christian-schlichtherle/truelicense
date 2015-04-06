/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core.auth;

import net.java.truelicense.core.codec.Codec;

import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

/**
 * A controller for storing authenticated objects (artifacts).
 * This interface is used by {@linkplain Authentication authentications} to
 * {@linkplain Authentication#sign sign} and
 * {@linkplain Authentication#verify verify} artifacts.
 *
 * @author Christian Schlichtherle
 */
@Immutable
public interface Repository {

    /** Returns the repository model. */
    RepositoryModel model();

    /**
     * Encodes and signs the given {@code artifact}
     * and returns an artifactory for decoding it.
     * As a side effect, the state of this repository is modified so that a
     * subsequent {@linkplain #verify verification} can succeed.
     *
     * @param  codec the codec for encoding the artifact.
     *         It is an implementation detail whether or not encoding
     *         {@code null} is supported or not.
     * @param  engine the signature engine.
     * @param  key the private key.
     * @param  artifact the nullable artifact to sign.
     *         This may be {@code null} if and only if the {@code codec}
     *         supports encoding it.
     */
    Artifactory sign(
            Codec codec,
            Signature engine,
            PrivateKey key,
            @Nullable Object artifact)
    throws Exception;

    /**
     * Verifies the signature of the encoded artifact
     * and returns an artifactory for decoding it.
     * The state of this repository is not modified by this method.
     *
     * @param  codec the codec for decoding the artifact.
     *         It is an implementation detail whether or not decoding
     *         {@code null} is supported or not.
     * @param  engine the signature engine.
     * @param  key the public key.
     * @throws IllegalStateException if the state of the model doesn't
     *         match the configuration of the codec or the signature engine.
     */
    Artifactory verify(
            Codec codec,
            Signature engine,
            PublicKey key)
    throws Exception;
}
