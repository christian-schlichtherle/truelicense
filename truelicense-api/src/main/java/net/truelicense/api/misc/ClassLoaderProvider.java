/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.api.misc;

import java.util.Optional;

/**
 * Provides an optional class loader.
 *
 * @author Christian Schlichtherle
 */
public interface ClassLoaderProvider {

    /**
     * Returns the optional class loader.
     * If no value is present then the system class loader gets used to load
     * classes and resources.
     */
    Optional<ClassLoader> classLoader();
}
