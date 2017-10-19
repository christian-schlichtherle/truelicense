/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.v2.commons.auth;

import net.truelicense.api.auth.RepositoryController;
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
    public RepositoryController controller(V2RepositoryModel model, Codec codec) {
        return new V2RepositoryController(model, codec);
    }
}
