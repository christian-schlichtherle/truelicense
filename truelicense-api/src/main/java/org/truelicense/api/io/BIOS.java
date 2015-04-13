/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.api.io;

import javax.annotation.CheckForNull;
import java.io.IOException;
import java.nio.file.Path;

/**
 * A Basic Input/Output System (BIOS).
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
     * If the given class loader is not {@code null}, then the resource gets
     * loaded as described in {@link ClassLoader#getResourceAsStream(String)}.
     * Otherwise, the resource gets loaded as described in
     * {@link ClassLoader#getSystemResourceAsStream(String)}.
     *
     * @param  name the name of the resource to load.
     * @param  loader the nullable class loader to use for loading the resource.
     *         If this is {@code null}, then the system class loader gets used.
     * @return A source which loads the resource with the given {@code name}.
     */
    Source resource(String name, @CheckForNull ClassLoader loader);

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
    Store systemNodeStore(Class<?> classInPackage, String key);

    /**
     * Returns a store for the user preferences node for the package of the
     * given class and the given key.
     */
    Store userNodeStore(Class<?> classInPackage, String key);
}
