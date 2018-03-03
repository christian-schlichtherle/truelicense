/*
 * Copyright (C) 2005-2017 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package net.truelicense.spi.io;

import global.namespace.fun.io.api.Socket;
import global.namespace.fun.io.api.Store;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.Optional;

/**
 * A Basic Input/Output System (BIOS).
 * Implementations need to be stateless.
 *
 * @author Christian Schlichtherle
 */
public interface BIOS {

    /** Copies the data from the given source to the given sink. */
    void copy(Socket<InputStream> input, Socket<OutputStream> output) throws IOException;

    /** Returns a new memory store. */
    Store memoryStore();

    /** Returns a store for the given path. */
    Store pathStore(Path path);

    /**
     * Returns an input stream socket which loads the resource with the given {@code name}.
     * If the given class loader list is empty, then the resource gets loaded
     * as described in {@link ClassLoader#getSystemResourceAsStream(String)}.
     * Otherwise, the resource gets loaded as described in
     * {@link ClassLoader#getResourceAsStream(String)} using the first
     * class loader in the list.
     *
     * @param  name the name of the resource to load.
     * @param  classLoader
     *         The optional class loader to use for loading the resource.
     *         If no value is present then the system class loader gets used.
     * @return An input stream socket which loads the resource with the given {@code name}.
     */
    Socket<InputStream> resource(String name, Optional<ClassLoader> classLoader);

    /** Returns an input stream socket which reads from standard input without ever closing it. */
    Socket<InputStream> stdin();

    /** Returns an output stream socket which writes to standard output without ever closing it. */
    Socket<OutputStream> stdout();

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
