/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.misc;

import javax.annotation.CheckForNull;

/**
 * Provides a class loader.
 *
 * @author Christian Schlichtherle
 */
public interface ClassLoaderProvider {
    /**
     * Returns a class loader or {@code null} if the system class loader
     * shall get used to load classes and resources.
     */
    @CheckForNull ClassLoader classLoader();
}
