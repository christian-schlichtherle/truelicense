/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.spi.misc;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * An option is an immutable list of at most one non-null item.
 * Using the static constructors of this class you can avoid programming with
 * {@code null}able references.
 *
 * @author Christian Schlichtherle
 */
public final class Option {

    /** Returns an empty list. */
    public static <T> List<T> none() { return Collections.emptyList(); }

    /**
     * Wraps the given nullable item in an immutable list of at most one
     * non-null item.
     * If the item is {@code null}, then the returned list is empty.
     * Otherwise, the returned list contains only the item.
     */
    public static <T> List<T> wrap(@Nullable T item) {
        return null == item
                ? Collections.<T>emptyList()
                : Collections.singletonList(item);
    }

    /**
     * Unwraps the first item from the given list.
     * If the list is empty, then {@code null} gets returned.
     * Otherwise, the first item in the list gets returned.
     */
    @SuppressWarnings("LoopStatementThatDoesntLoop")
    public static @Nullable <T> T unwrap(final List<T> option) {
        for (T item : option)
            return item;
        return null;
    }
}
