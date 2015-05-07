/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.auth;

import org.truelicense.api.codec.Decoder;

/**
 * Provides authentication services.
 *
 * @author Christian Schlichtherle
 */
public interface Authentication {

    /**
     * Encodes and signs the given {@code artifact} and returns a decoder for
     * it.
     * As a side effect, the state of the underlying repository model is updated
     * with the encoded artifact and its signature so that a subsequent
     * {@linkplain #verify verification} can succeed.
     *
     * @param repository the controller for the repository for encoding the
     *                   artifact to.
     * @param artifact the artifact to sign.
     * @return An Artifactory for decoding the signed artifact in the
     *         repository.
     */
    Decoder sign(RepositoryController repository, Object artifact) throws Exception;

    /**
     * Verifies the signature of the encoded artifact in the underlying
     * repository model and returns a decoder for it.
     * The state of the underlying repository model is not modified by this
     * method.
     *
     * @param repository the controller for the repository for decoding the
     *                   artifact from.
     * @return An Artifactory for decoding the verified artifact in the
     *         repository.
     * @throws RepositoryIntegrityException if the integrity of the repository
     *         with its encoded artifact has been compromised.
     */
    Decoder verify(RepositoryController repository) throws Exception;
}
