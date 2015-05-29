/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.spi.io;

import org.truelicense.api.io.Sink;
import org.truelicense.api.io.Source;
import org.truelicense.api.io.Store;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * A Basic Input/Output System (BIOS).
 * Implementations need to be stateless.
 *
 * @author Christian Schlichtherle
 */
public interface BIOS {

    /** Copies the data from the given source to the given sink. */
    void copy(Source source, Sink sink) throws IOException;

    /** Returns a new memory store. */
    Store memoryStore();

    /** Returns a store for the given path. */
    Store pathStore(Path path);

    /**
     * Returns a source which loads the resource with the given {@code name}.
     * If the given class loader list is empty, then the resource gets loaded
     * as described in {@link ClassLoader#getSystemResourceAsStream(String)}.
     * Otherwise, the resource gets loaded as described in
     * {@link ClassLoader#getResourceAsStream(String)} using the first
     * class loader in the list.
     *
     * @param  name the name of the resource to load.
     * @param  classLoader
     *         The optional class loader to use for loading the resource.
     *         This is a list of at most one non-null item.
     *         The list may be empty to indicate that the system class loader
     *         shall get used.
     * @return A source which loads the resource with the given {@code name}.
     */
    Source resource(String name, List<ClassLoader> classLoader);

    /**
     * Returns a source which reads from standard input without ever closing it.
     */
    Source stdin();

    /**
     * Returns a sink which writes to standard output without ever closing it.
     */
    Sink stdout();

    /**
     * Returns a store for the system preferences node for the package of the
     * given class and the given key.
     */
    Store systemPreferencesStore(Class<?> classInPackage, String key);

    /**
     * Returns a store for the user preferences node for the package of the
     * given class and the given key.
     */
    Store userPreferencesStore(Class<?> classInPackage, String key);
}
