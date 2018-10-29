/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
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
public interface RepositoryContext<Model> {

    /** Returns a new repository model. */
    Model model();

    /** Configures the repository context to use the given codec. */
    WithCodec<Model> with(Codec codec);

    interface WithCodec<Model> {

        /** Returns a repository controller for the given repository model using the configured codec. */
        RepositoryController controller(Model model);
    }
}
