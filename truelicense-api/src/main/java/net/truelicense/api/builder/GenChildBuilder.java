/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api.builder;

/**
 * Builds a child component in a hierarchical component structure and injects it into some parent component builder.
 *
 * @param <ParentBuilder> the type of the parent component builder.
 * @author Christian Schlichtherle
 */
public interface GenChildBuilder<ParentBuilder extends GenBuilder<?>> {

    /** Builds the child component, injects it into the parent component builder and returns the latter. */
    ParentBuilder up();
}