/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.v1.auth;

import de.schlichtherle.xml.GenericCertificate;
import net.truelicense.api.auth.RepositoryContext;
import net.truelicense.api.codec.Codec;

/**
 * A repository context for use with V1 format license keys.
 *
 * @author Christian Schlichtherle
 */
public final class V1RepositoryContext implements RepositoryContext<GenericCertificate> {

    @Override
    public GenericCertificate model() { return new GenericCertificate(); }

    @Override
    public WithCodec<GenericCertificate> with(Codec codec) {
        return model -> new V1RepositoryController(codec, model);
    }
}
