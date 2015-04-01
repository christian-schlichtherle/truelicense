/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truelicense.core.util;

/**
 * Provides a context.
 *
 * @author Christian Schlichtherle
 * @since  TrueLicense 2.2
 */
public interface ContextProvider<T> {
    /** Returns the context from which this object originated. */
    T context();
}
