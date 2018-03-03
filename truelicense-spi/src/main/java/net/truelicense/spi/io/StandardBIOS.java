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
 * The standard BIOS implementation.
 * This class is immutable.
 *
 * @author Christian Schlichtherle
 */
public class StandardBIOS implements BIOS {

    /**
     * {@inheritDoc}
     * <p>
     * The implementation in the class {@link StandardBIOS} is suitable for only
     * small amounts of data, say a few kilobytes.
     */
    @Override
    public void copy(Socket<InputStream> input, Socket<OutputStream> output) throws IOException {
        try {
            global.namespace.fun.io.bios.BIOS.copy(input, output);
        } catch (IOException e) {
            throw (IOException) e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    @Override
    public Store memoryStore() { return global.namespace.fun.io.bios.BIOS.memoryStore(); }

    @Override
    public Store pathStore(Path path) { return global.namespace.fun.io.bios.BIOS.pathStore(path); }

    @Override
    public Socket<InputStream> resource(String name, Optional<ClassLoader> classLoader) {
        return classLoader
                .map(cl -> global.namespace.fun.io.bios.BIOS.resource(name, cl))
                .orElseGet(() -> global.namespace.fun.io.bios.BIOS.resource(name));
    }

    @Override
    public Socket<InputStream> stdin() { return global.namespace.fun.io.bios.BIOS.stdin(); }

    @Override
    public Socket<OutputStream> stdout() { return global.namespace.fun.io.bios.BIOS.stdout(); }

    @Override
    public Store systemPreferencesStore(Class<?> classInPackage, String key) {
        return global.namespace.fun.io.bios.BIOS.systemPreferencesStore(classInPackage, key);
    }

    @Override
    public Store userPreferencesStore(Class<?> classInPackage, String key) {
        return global.namespace.fun.io.bios.BIOS.userPreferencesStore(classInPackage, key);
    }
}
