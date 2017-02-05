/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.v1.auth;

import de.schlichtherle.xml.GenericCertificate;
import org.truelicense.api.auth.RepositoryController;
import org.truelicense.api.auth.RepositoryContext;
import org.truelicense.api.codec.Codec;

/**
 * A repository context for use with V1 format license keys.
 *
 * @author Christian Schlichtherle
 */
public final class V1RepositoryContext
implements RepositoryContext<GenericCertificate> {

    @Override
    public GenericCertificate model() {
        return new GenericCertificate();
    }

    @Override
    public RepositoryController controller(GenericCertificate model, Codec codec) {
        return new V1RepositoryController(model, codec);
    }
}
