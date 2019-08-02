/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.v2.core.auth;

import global.namespace.truelicense.api.auth.RepositoryController;
import global.namespace.truelicense.api.auth.RepositoryFactory;
import global.namespace.truelicense.api.codec.Codec;

/**
 * A repository context for use with V2 format license keys.
 */
public final class V2RepositoryFactory implements RepositoryFactory<V2RepositoryModel> {

    @Override
    public V2RepositoryModel model() {
        return new V2RepositoryModel();
    }

    @Override
    public RepositoryController controller(Codec codec, V2RepositoryModel model) {
        return new V2RepositoryController(codec, model);
    }
}
