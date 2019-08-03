/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.v1;

import de.schlichtherle.xml.GenericCertificate;
import global.namespace.truelicense.api.auth.RepositoryController;
import global.namespace.truelicense.api.auth.RepositoryFactory;
import global.namespace.truelicense.api.codec.Codec;

/**
 * A repository factory for use with V1 format license keys.
 */
final class V1RepositoryFactory implements RepositoryFactory<GenericCertificate> {

    @Override
    public GenericCertificate model() {
        return new GenericCertificate();
    }

    @Override
    public RepositoryController controller(Codec codec, GenericCertificate model) {
        return new V1RepositoryController(codec, model);
    }
}
