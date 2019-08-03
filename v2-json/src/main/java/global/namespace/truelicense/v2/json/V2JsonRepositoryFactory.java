/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.v2.json;

import global.namespace.truelicense.api.auth.RepositoryController;
import global.namespace.truelicense.api.auth.RepositoryFactory;
import global.namespace.truelicense.api.codec.Codec;

/**
 * A repository factory for use with V2/JSON format license keys.
 */
final class V2JsonRepositoryFactory implements RepositoryFactory<V2JsonRepositoryModel> {

    @Override
    public V2JsonRepositoryModel model() {
        return new V2JsonRepositoryModel();
    }

    @Override
    public RepositoryController controller(Codec codec, V2JsonRepositoryModel model) {
        return new V2JsonRepositoryController(codec, model);
    }
}
