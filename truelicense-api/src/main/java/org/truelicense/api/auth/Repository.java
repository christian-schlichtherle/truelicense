/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.auth;

import java.security.Signature;

/**
 * A controller for storing authenticated objects (artifacts) in a generic
 * repository model.
 * A repository controller is used by an
 * {@linkplain Authentication authentication} to
 * {@linkplain Authentication#sign sign} and
 * {@linkplain Authentication#verify verify} an arbitrary artifact.
 * A repository controller is created by a
 * {@linkplain RepositoryContext repository context}.
 *
 * @author Christian Schlichtherle
 */
public interface Repository {

    /**
     * Encodes and signs the given {@code artifact}, stores it in the underlying
     * repository model and returns an artifactory for decoding it.
     * As a side effect, the state of the underlying repository is updated
     * so that a subsequent {@linkplain #verify verification} can succeed.
     *
     * @param engine
     *        the signature engine to use.
     *        The engine must be initialized for signing before calling this
     *        method.
     * @param artifact
     *        the artifact to sign.
     */
    Artifactory sign(Signature engine, Object artifact) throws Exception;

    /**
     * Verifies the signature of the encoded artifact in the underlying
     * repository model and returns an artifactory for decoding it.
     * The state of the underlying repository model is not modified by this
     * method.
     *
     * @param engine
     *        the signature engine to use.
     *        The engine must be initialized for verifying before calling this
     *        method.
     * @throws RepositoryIntegrityException if the integrity of the underlying
     *         repository model has been compromised.
     */
    Artifactory verify(Signature engine) throws Exception;
}
