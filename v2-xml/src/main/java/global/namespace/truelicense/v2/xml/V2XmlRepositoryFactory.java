/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.v2.xml;

import global.namespace.truelicense.api.auth.RepositoryController;
import global.namespace.truelicense.api.auth.RepositoryFactory;
import global.namespace.truelicense.api.codec.Codec;

/**
 * A repository factory for use with V2/XML format license keys.
 */
final class V2XmlRepositoryFactory implements RepositoryFactory<V2XmlRepositoryModel> {

    @Override
    public V2XmlRepositoryModel model() {
        return new V2XmlRepositoryModel();
    }

    @Override
    public Class<V2XmlRepositoryModel> modelClass() {
        return V2XmlRepositoryModel.class;
    }

    @Override
    public RepositoryController controller(Codec codec, V2XmlRepositoryModel model) {
        return new V2XmlRepositoryController(codec, model);
    }
}
