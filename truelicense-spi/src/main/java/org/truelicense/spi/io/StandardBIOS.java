/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.spi.io;

import org.truelicense.api.io.Sink;
import org.truelicense.api.io.Source;
import org.truelicense.api.io.Store;
import org.truelicense.spi.misc.Option;

import java.io.*;
import java.nio.file.Path;
import java.util.List;
import java.util.prefs.Preferences;

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
    public void copy(final Source source, final Sink sink) throws IOException {
        try (InputStream in = source.input();
             OutputStream out = sink.output()) {
            final byte[] buffer = new byte[Store.BUFSIZE];
            int read;
            while (0 <= (read = in.read(buffer)))
                out.write(buffer, 0, read);
        }
    }

    @Override
    public Store memoryStore() {
        return new MemoryStore();
    }

    @Override
    public Store pathStore(Path path) {
        return new PathStore(path);
    }

    @Override
    @SuppressWarnings("LoopStatementThatDoesntLoop")
    public Source resource(
            final String name,
            final List<ClassLoader> classLoader) {
        return new Source() {
            @Override public InputStream input() throws IOException {
                for (InputStream in : inputStream())
                    return in;
                throw new FileNotFoundException(name);
            }

            List<InputStream> inputStream() {
                for (ClassLoader cl : classLoader)
                    return Option.wrap(cl.getResourceAsStream(name));
                return Option.wrap(ClassLoader.getSystemResourceAsStream(name));
            }
        };
    }

    @Override
    public Source stdin() { return uncloseable(System.in); }

    @Override
    public Sink stdout() { return uncloseable(System.out); }

    private static Source uncloseable(final InputStream in) {
        return new Source() {
            @Override public InputStream input() {
                return new FilterInputStream(in) {
                    @Override public void close() { }
                };
            }
        };
    }

    private static Sink uncloseable(final OutputStream out) {
        return new Sink() {
            @Override public OutputStream output() {
                return new FilterOutputStream(out) {
                    @Override public void close() throws IOException {
                        out.flush();
                    }
                };
            }
        };
    }

    @Override
    public Store systemPreferencesStore(Class<?> classInPackage, String key) {
        return new PreferencesStore(
                Preferences.systemNodeForPackage(classInPackage), key);
    }

    @Override
    public Store userPreferencesStore(Class<?> classInPackage, String key) {
        return new PreferencesStore(
                Preferences.userNodeForPackage(classInPackage), key);
    }
}
