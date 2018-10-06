/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.v2.core.auth;

import net.truelicense.api.auth.RepositoryContext;
import net.truelicense.api.codec.Codec;

/**
 * A repository context for use with V2 format license keys.
 *
 * @author Christian Schlichtherle
 */
public final class V2RepositoryContext implements RepositoryContext<V2RepositoryModel> {

    @Override
    public V2RepositoryModel model() { return new V2RepositoryModel(); }

    @Override
    public WithCodec<V2RepositoryModel> with(Codec codec) {
        return model -> new V2RepositoryController(codec, model);
    }
}
