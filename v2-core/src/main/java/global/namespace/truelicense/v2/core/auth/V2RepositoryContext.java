/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.v2.core.auth;

import global.namespace.truelicense.api.auth.RepositoryContext;
import global.namespace.truelicense.api.codec.Codec;

/**
 * A repository context for use with V2 format license keys.
 */
public final class V2RepositoryContext implements RepositoryContext<V2RepositoryModel> {

    @Override
    public V2RepositoryModel model() { return new V2RepositoryModel(); }

    @Override
    public WithCodec<V2RepositoryModel> with(Codec codec) {
        return model -> new V2RepositoryController(codec, model);
    }
}
