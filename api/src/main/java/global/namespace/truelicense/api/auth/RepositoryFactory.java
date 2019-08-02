/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.api.auth;

import global.namespace.truelicense.api.codec.Codec;

/**
 * A factory for repository models and their associated
 * {@linkplain RepositoryController repository controllers}.
 *
 * @param <Model> the generic repository model.
 */
public interface RepositoryFactory<Model> {

    /** Returns a new repository model. */
    Model model();

    /**
     * Returns a new repository controller using the given codec and repository model.
     */
    RepositoryController controller(Codec codec, Model model);
}
