/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.misc;

import java.util.List;

/**
 * Provides an optional class loader.
 *
 * @author Christian Schlichtherle
 */
public interface ClassLoaderProvider {

    /**
     * Returns the optional class loader.
     * This is a list of at most one non-null item.
     * The list may be empty to indicate that the system class loader shall get
     * used to load classes and resources.
     */
    List<ClassLoader> classLoader();
}
