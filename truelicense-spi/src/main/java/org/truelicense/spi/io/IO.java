/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */

package org.truelicense.spi.io;

import org.truelicense.api.io.Sink;
import org.truelicense.api.io.Source;
import org.truelicense.api.io.Store;

import javax.annotation.CheckForNull;
import javax.annotation.concurrent.Immutable;
import java.io.*;

/**
 * Provides common I/O functions.
 *
 * @author Christian Schlichtherle
 */
@Immutable
public final class IO {

    /*
     * Copies the data from the given source to the given sink.
     * The implementation in this method is suitable for only small amounts of
     * data, say a few kilobytes.
     *
     * @param source the input source.
     * @param sink the output sink.
     */
    public static void copy(final Source source, final Sink sink)
    throws IOException {
        try (InputStream in = source.input();
             OutputStream out = sink.output()) {
            final byte[] buffer = new byte[Store.BUFSIZE];
            int read;
            while (0 <= (read = in.read(buffer)))
                out.write(buffer, 0, read);
        }
    }

    /**
     * Returns a source which loads the resource with the given {@code name}.
     * This method will use the given class to resolve the resource name and
     * the class loader as described in
     * {@link Class#getResourceAsStream(String)}.
     *
     * @param  name the name of the resource to load.
     * @param  clazz the class to use for loading the resource.
     * @return A source which loads the resource with the given {@code name}.
     */
    public static Source resource(final String name, final Class<?> clazz) {
        return new Source() {
            @Override public InputStream input() throws IOException {
                return check(clazz.getResourceAsStream(name), name);
            }
        };
    }

    /**
     * Returns a source which loads the resource with the given {@code name}.
     * If the given class loader is not {@code null}, then the resource will
     * get loaded as described in
     * {@link ClassLoader#getResourceAsStream(String)}.
     * Otherwise, the resource will get loaded as described in
     * {@link ClassLoader#getSystemResourceAsStream(String)}.
     *
     * @param  name the name of the resource to load.
     * @param  loader the nullable class loader to use for loading the resource.
     *         If this is {@code null}, then the system class loader will get
     *         used.
     * @return A source which loads the resource with the given {@code name}.
     */
    public static Source resource(
            final String name,
            final @CheckForNull ClassLoader loader) {
        return new Source() {
            @Override public InputStream input() throws IOException {
                return check(null != loader
                        ? loader.getResourceAsStream(name)
                        : ClassLoader.getSystemResourceAsStream(name), name);
            }
        };
    }

    private static InputStream check(final @CheckForNull InputStream in, final String name)
            throws FileNotFoundException {
        if (null == in) throw new FileNotFoundException(name);
        return in;
    }

    /**
     * Returns a source which reads from standard input without ever closing it.
     */
    public static Source input() { return uncloseable(System.in); }

    /**
     * Returns a sink which writes to standard output without ever closing it.
     */
    public static Sink output() { return uncloseable(System.out); }

    /**
     * Returns a sink which writes to standard error without ever closing it.
     */
    public static Sink error() { return uncloseable(System.err); }

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

    private IO() { }
}
