/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.auth;

import org.truelicense.api.codec.Codec;

/**
 * Defines authentication services.
 *
 * @author Christian Schlichtherle
 */
public interface Authentication extends KeyStoreParametersProvider {

    /**
     * Encodes and signs the given {@code artifact} in the provided
     * repository and returns an artifactory for decoding it.
     * After calling this method, the repository typically gets encoded for
     * future decoding and verification.
     *
     * @param  codec the codec for encoding the artifact.
     * @param  repository the repository for encoding the artifact to.
     * @param  artifact the artifact to sign.
     * @return An Artifactory for decoding the signed artifact in the
     *         repository.
     */
    Artifactory sign(Codec codec, Repository repository, Object artifact) throws Exception;

    /**
     * Verifies the signature of the encoded artifact in the provided
     * repository and returns an artifactory for decoding it.
     * Calling this method generally requires prior initialization of the
     * repository.
     * Though initialization is a side effect of calling {@link #sign}, this is
     * typically done by decoding the repository itself.
     *
     * @param  codec the codec for decoding the artifact.
     * @param  repository the repository for decoding the artifact from.
     * @return An Artifactory for decoding the verified artifact in the
     *         repository.
     * @throws RepositoryIntegrityException if the integrity of the repository
     *         with its encoded artifact has been compromised.
     */
    Artifactory verify(Codec codec, Repository repository) throws Exception;
}
