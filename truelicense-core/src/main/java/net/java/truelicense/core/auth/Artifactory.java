/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core.auth;

import javax.annotation.Nullable;
import java.lang.reflect.Type;

/**
 * An immutable factory for authenticated objects (artifacts).
 *
 * @author Christian Schlichtherle
 */
public interface Artifactory {

    /**
     * Decodes the artifact from its encoded form.
     *
     * @param  <T> the expected generic type of the decoded artifact.
     * @param  expected the expected generic type of the decoded artifact,
     *         e.g. {@code String.class}.
     *         This is just a hint and the implementation may ignore it.
     * @return A duplicate of the original artifact.
     *         Its actual type may differ from the expected generic type.
     *         This may be {@code null} if and only if the original artifact
     *         was {@code null}.
     */
    @Nullable <T> T decode(Type expected) throws Exception;
}
