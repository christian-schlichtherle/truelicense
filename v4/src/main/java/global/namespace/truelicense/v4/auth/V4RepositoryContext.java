/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.v4.auth;

import global.namespace.truelicense.api.auth.RepositoryContext;
import global.namespace.truelicense.api.codec.Codec;

/**
 * A repository context for use with V2 format license keys.
 */
public final class V4RepositoryContext implements RepositoryContext<V4RepositoryModel> {

    @Override
    public V4RepositoryModel model() {
        return new V4RepositoryModel();
    }

    @Override
    public WithCodec<V4RepositoryModel> with(Codec codec) {
        return model -> new V4RepositoryController(codec, model);
    }
}
