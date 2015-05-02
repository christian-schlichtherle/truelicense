/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.v2.commons.auth;

import org.truelicense.api.auth.RepositoryController;
import org.truelicense.api.auth.RepositoryContext;
import org.truelicense.api.codec.Codec;

/**
 * A repository context for use with V2 format license keys.
 *
 * @author Christian Schlichtherle
 */
public final class V2RepositoryContext
implements RepositoryContext<V2RepositoryModel> {

    @Override
    public V2RepositoryModel model() {
        return new V2RepositoryModel();
    }

    @Override
    public RepositoryController controller(V2RepositoryModel model, Codec codec) {
        return new V2RepositoryController(model, codec);
    }
}
