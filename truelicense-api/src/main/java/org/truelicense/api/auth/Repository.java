/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.auth;

import org.truelicense.api.codec.Codec;

import java.security.Signature;

/**
 * A controller for storing authenticated objects (artifacts) in a generic
 * repository model.
 * Repository controllers are used by
 * {@linkplain Authentication authentications} to
 * {@linkplain Authentication#sign sign} and
 * {@linkplain Authentication#verify verify} arbitrary artifacts.
 * A repository controller needs to be configured with a
 * {@link org.truelicense.api.codec.Codec} for encoding and decoding any
 * given artifacts.
 *
 * @author Christian Schlichtherle
 */
public interface Repository {

    /**
     * Encodes and signs the given {@code artifact}, stores it in the underlying
     * repository model and returns an artifactory for decoding it.
     * As a side effect, the state of the underlying repository model is updated
     * so that a subsequent {@linkplain #verify verification} can succeed.
     *
     * @param codec
     *        the codec for encoding the artifact.
     * @param engine
     *        the signature engine to use.
     *        The engine must be initialized for signing before calling this
     *        method.
     * @param artifact
     *        the artifact to sign.
     */
    Artifactory sign(Codec codec, Signature engine, Object artifact) throws Exception;

    /**
     * Verifies the signature of the artifact which is encoded in the underlying
     * repository model and returns an artifactory for decoding it.
     * The state of the underlying repository model is not modified by this
     * method.
     *
     * @param codec
     *        the codec for decoding the artifact.
     * @param engine
     *        the signature engine to use.
     *        The engine must be initialized for verifying before calling this
     *        method.
     * @throws RepositoryIntegrityException if the integrity of the underlying
     *         repository model has been compromised.
     */
    Artifactory verify(Codec codec, Signature engine) throws Exception;
}
