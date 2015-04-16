/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.java.truelicense.core.util;

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
