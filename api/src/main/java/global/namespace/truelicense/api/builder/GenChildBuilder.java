/*
 * Copyright (C) 2005 - 2019 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package global.namespace.truelicense.api.builder;

/**
 * Builds a child component in a hierarchical component structure and injects it into some parent builder.
 *
 * @param <ParentBuilder> the type of the parent builder.
 */
public interface GenChildBuilder<ParentBuilder extends GenBuilder<?>> {

    /** Builds the child component, injects it into the parent builder and returns the latter. */
    ParentBuilder up();
}
